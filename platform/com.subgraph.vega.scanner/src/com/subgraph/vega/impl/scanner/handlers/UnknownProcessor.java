package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class UnknownProcessor implements ICrawlerResponseProcessor {
	private final ParametricCheckHandler parametricChecks = new ParametricCheckHandler();
	private final ICrawlerResponseProcessor fetchFileProcessor = new FileProcessor();
	private final ICrawlerResponseProcessor fetchDirProcessor = new DirectoryProcessor();

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		ctx.debug(ps + "UnknownProcess callback with idx = "+ ctx.getCurrentIndex());
		if(ctx.getCurrentIndex() == 0) {
			processInitialResponse(request, response, ctx, ps);
		} else {
			processProbeResponses(request, response, ctx, ps);
		}
	}
	
	private void processInitialResponse(HttpUriRequest request, IHttpResponse response, IModuleContext ctx, IPathState ps) {
		ps.setResponse(response);
		if(response.isFetchFail()) {
			ctx.error(request, response, "during initial resource fetch");
			return;
		}
		ps.getPath().setVisited(true);
		final IPageFingerprint fp = response.getPageFingerprint();
		final IPathState par = ps.get404Parent();
		final int rcode = response.getResponseCode();

		if((par == null && rcode == 404) || (ps.hasParent404Fingerprint(fp))) {
			ps.setPageMissing();
			ps.unlockChildren();
			ctx.debug(ps + " Starting parametric checks on unknown path because page is missing.");
			parametricChecks.initialize(ps);
			return;
		}
		
		if(par != null && !response.getBodyAsString().isEmpty() && rcode == 200 && fp.isSame(par.getUnknownFingerprint())) {
			ctx.debug(ps +"Unknown path fetch matches parent unknown fp, processing as a file.");
			ps.getPath().setPathType(PathType.PATH_FILE);
			fetchFileProcessor.processResponse(null, request, response, ctx);
			return;
		}
		
		if(par != null && rcode >= 300 && rcode < 400 && fp.isSame(par.getUnknownFingerprint()) && fp.isSame(par.getPathFingerprint())) {
			ctx.debug(ps + "Unknown path fetch matches both parent probes, processing as file.");
			ps.getPath().setPathType(PathType.PATH_FILE);
			fetchFileProcessor.processResponse(null, request, response, ctx);
			return;
		}
		
		ctx.debug(ps + " Sending probes to resolve unknown path.");
		sendProbeRequests(ps);
	}

	private void sendProbeRequests(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		ctx.submitAlteredRequest(this, "/", 1);
		ctx.submitAlteredRequest(this, "/abc123/", 2);
	}

	private void processProbeResponses(HttpUriRequest request, IHttpResponse response, IModuleContext ctx, IPathState ps) {
		if(response.isFetchFail()) {
			ctx.error(request, response, "Fetch failed processing unknown path probe responses");
			callFetchHandler(ctx, ps);
		}
		ctx.addRequestResponse(request, response);
		ctx.incrementResponseCount();
		if(ctx.allResponsesReceived())
			analyzeResponses(ctx, ps);
		else
			ctx.debug("Not all received yet");
	}

	private void analyzeResponses(IModuleContext ctx, IPathState ps) {
		
		// http://host.com/foo/bar.php/ vs. http://host.com/foo/bar.php/abc123/ vs http://host.com/foo/bar.php
		if(ctx.isFingerprintMatch(1, 2) && ctx.isFingerprintMatch(2, ps.getPathFingerprint())) {
			ctx.debug(ps + "Probes all match for unknown path, processing as file.");
			ps.getPath().setPathType(PathType.PATH_FILE);
			callFetchHandler(ctx, ps);
			return;
		}
		
		final IHttpResponse res1 = ctx.getSavedResponse(1);
		if(ctx.isFingerprintMatch(1, ps.getPathFingerprint())) {
			ctx.debug(ps + "Trailing / probe matches initial path fetch, processing as directory.");
			assumeDirectory(res1, ctx, ps);
			return;
		}
		
		// pivot code, response code
		final int pcode = ps.getResponse().getResponseCode();
		final int rcode = res1.getResponseCode();
		if(rcode == 404 && pcode >= 300 && pcode < 400) {
			if(hasLocationHeaderWithRequestUri(ps, ctx.getSavedRequest(1))) {
				ctx.debug(ps + "Trailing slash probe matches redirect from initial fetch, processing as directory");
				ps.setSureDirectory();
				assumeDirectory(res1, ctx, ps);
				return;
			}
		}
		
		final IPathState p404 = ps.get404Parent();
		if( ((p404 == null) && rcode == 404) || p404.has404FingerprintMatching(res1.getPageFingerprint())) {
			ctx.debug(ps + "Trailing slash probe looks like a 404, processing as a file");
			ps.getPath().setPathType(PathType.PATH_FILE);
		} else if(pcode  < 300 && rcode >= 300 && ps.getResponse().getBodyAsString().length() > 0) {
			ctx.debug(ps + "Trailing slash probe returned code 3xx - 5xx and initial fetch was a 200, processing as file");
			ps.getPath().setPathType(PathType.PATH_FILE);
		} else {
			ctx.debug(ps + "No idea what this is, try processing as file");
		}
		
		callFetchHandler(ctx, ps);
	}
	
	private boolean hasLocationHeaderWithRequestUri(IPathState ps, HttpUriRequest req) {
		final String locationValue = getLocationHeader(ps);
		if(locationValue == null)
			return false;
		final String uriString = req.getURI().toString();
		return locationValue.equalsIgnoreCase(uriString);
		
	}
	private String getLocationHeader(IPathState ps) {
		final HttpResponse rr = ps.getResponse().getRawResponse();
		final Header lh = rr.getFirstHeader("Location");
		if(lh == null)
			return null;
		return lh.getValue();
	}

	private void assumeDirectory(IHttpResponse response, IModuleContext ctx, IPathState ps) {
		ps.getPath().setPathType(PathType.PATH_DIRECTORY);
		ps.setUnknownFingerprint(ps.getPathFingerprint());
		ps.setResponse(response);
		callFetchHandler(ctx, ps);
	}
	
	private void callFetchHandler(IModuleContext ctx, IPathState ps) {
		final IWebPath path = ps.getPath();
		final HttpUriRequest req = ps.createRequest();
		final IHttpResponse res = ps.getResponse();
		if(path.getPathType() == PathType.PATH_DIRECTORY || path.getParentPath() == null) 
			fetchDirProcessor.processResponse(null, req, res, ctx);
		else
			fetchFileProcessor.processResponse(null, req, res, ctx);
	}
}
