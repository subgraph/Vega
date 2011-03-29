package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class UnknownCheckHandler implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor fetchFileProcessor = new FileProcessor();
	private final ICrawlerResponseProcessor fetchDirProcessor = new DirectoryProcessor();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		final IHttpResponse stateRes = ps.getResponse();
		final HttpUriRequest stateReq = ps.createRequest();
		if(response.isFetchFail()) {
			ctx.error(request, response, "during node type check");
			scheduleNext(crawler, stateReq, stateRes, ctx, ps);
			return;
		}
		
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		
		if(ctx.incrementResponseCount() < 2)
			return;
		
		
		final IHttpResponse res0 = ctx.getSavedResponse(0);
		final HttpUriRequest req0 = ctx.getSavedRequest(0);
		final IPageFingerprint fp0 = ctx.getSavedFingerprint(0);
		
		if(ctx.isFingerprintMatch(0, 1) && ctx.isFingerprintMatch(1, ps.getPathFingerprint())) {
			ps.getPath().setPathType(PathType.PATH_FILE);
			scheduleNext(crawler, stateReq, stateRes, ctx, ps);
			return;
		}
		
		if(ctx.isFingerprintMatch(0, ps.getPathFingerprint())) {
			assumeDir(crawler, stateReq, stateRes, ctx, ps);
			return;
		}

		final HttpResponse r = ps.getResponse().getRawResponse();
		
		final int code = ps.getResponse().getResponseCode();
		if(response.getResponseCode() == 404 && code >= 300 && code < 400) {
			final Header locationHeader = r.getFirstHeader("Location");
			if(locationHeader != null) {
				final String loc = locationHeader.getValue();
				if(loc != null && loc.equalsIgnoreCase(req0.getURI().toString())) {
					ps.setSureDirectory();
					assumeDir(crawler, stateReq, stateRes, ctx, ps);
					return;
				}
			}
		}
		
			
		final IPathState p404 = ps.get404Parent();
			
		if( ((p404 == null) && res0.getResponseCode() == 404) ||  ps.hasParent404Fingerprint(fp0) || (code  < 300 && res0.getResponseCode() >= 300 && ps.getResponse().getBodyAsString().length() > 0)) {
			ps.getPath().setPathType(PathType.PATH_FILE);
			scheduleNext(crawler, stateReq, stateRes, ctx, ps);
		}
	}
	
	private void assumeDir(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, IModuleContext ctx, IPathState ps) {
		// XXX no_505_dir check
		ctx.debug("assuming "+ request.getURI() + " is directory");
		ps.getPath().setPathType(PathType.PATH_DIRECTORY);
		if(ps.getResponse() != null) 
			ps.setUnknownFingerprint(ps.getPathFingerprint());
		
		ps.setResponse(response);
		scheduleNext(crawler, request, response, ctx, ps);
	}
	private void scheduleNext(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, IModuleContext ctx, IPathState ps) {
		
		if(ps.getPath().getPathType() == PathType.PATH_DIRECTORY || ps.getPath().getParentPath() == null) {
			fetchDirProcessor.processResponse(crawler, request, response, ctx);
		} else {
			ctx.debug("Assuming "+ request.getURI() +" is file");
			fetchFileProcessor.processResponse(crawler, request, response, ctx);
		}
	}
}
