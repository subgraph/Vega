/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
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
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class UnknownProcessor implements ICrawlerResponseProcessor {
	private final ParametricCheckHandler parametricChecks = new ParametricCheckHandler();
	private final ICrawlerResponseProcessor fetchFileProcessor = new FileProcessor();
	private final ICrawlerResponseProcessor fetchDirProcessor = new DirectoryProcessor();

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		if(ctx.getCurrentIndex() == 0) {
			processInitialResponse(request, response, ctx, ps);
		} else {
			processProbeResponses(request, response, ctx, ps);
		}
	}

	private void processInitialResponse(HttpUriRequest request, IHttpResponse response, IInjectionModuleContext ctx, IPathState ps) {
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
			ctx.debug("Starting parametric checks on unknown path because page is missing.");
			parametricChecks.initialize(ps);
			return;
		}

		if(par != null && !response.getBodyAsString().isEmpty() && rcode == 200 && fp.isSame(par.getUnknownFingerprint())) {
			ctx.debug("Unknown path fetch matches parent unknown fp, processing as a file.");
			ps.getPath().setPathType(PathType.PATH_FILE);
			fetchFileProcessor.processResponse(null, request, response, ctx);
			return;
		}

		if(par != null && rcode >= 300 && rcode < 400 && fp.isSame(par.getUnknownFingerprint()) && fp.isSame(par.getPathFingerprint())) {
			ctx.debug("Unknown path fetch matches both parent probes, processing as file.");
			ps.getPath().setPathType(PathType.PATH_FILE);
			fetchFileProcessor.processResponse(null, request, response, ctx);
			return;
		}

		ctx.debug("Sending probes to resolve unknown path.");
		sendProbeRequests(ps);
	}

	private void sendProbeRequests(IPathState ps) {
		final IInjectionModuleContext ctx = ps.createModuleContext();
		ctx.submitAlteredRequest(this, "/", 1);
		ctx.submitAlteredRequest(this, "/abc123/", 2);
	}

	private void processProbeResponses(HttpUriRequest request, IHttpResponse response, IInjectionModuleContext ctx, IPathState ps) {
		if(response.isFetchFail()) {
			ctx.error(request, response, "Fetch failed processing unknown path probe responses");
			callFetchHandler(ctx, ps);
		}
		ctx.addRequestResponse(request, response);
		ctx.incrementResponseCount();
		if(ctx.allResponsesReceived())
			analyzeResponses(ctx, ps);
	}

	private void analyzeResponses(IInjectionModuleContext ctx, IPathState ps) {

		// http://host.com/foo/bar.php/ vs. http://host.com/foo/bar.php/abc123/ vs http://host.com/foo/bar.php
		if(ctx.isFingerprintMatch(1, 2) && ctx.isFingerprintMatch(2, ps.getPathFingerprint())) {
			ctx.debug("Probes all match for unknown path, processing as file.");
			ps.getPath().setPathType(PathType.PATH_FILE);
			callFetchHandler(ctx, ps);
			return;
		}

		final IHttpResponse res1 = ctx.getSavedResponse(1);
		if(ctx.isFingerprintMatch(1, ps.getPathFingerprint())) {
			ctx.debug("Trailing / probe matches initial path fetch, processing as directory.");
			assumeDirectory(res1, ctx, ps);
			return;
		}

		// pivot code, response code
		final int pcode = ps.getResponse().getResponseCode();
		final int rcode = res1.getResponseCode();
		if(pcode >= 300 && pcode < 400) {
			if(hasLocationHeaderWithRequestUri(ps, ctx.getSavedRequest(1))) {
				ctx.debug("Trailing slash probe matches redirect from initial fetch, processing as directory");
				ps.setSureDirectory();
				assumeDirectory(res1, ctx, ps);
				return;
			}
		}

		if(isProbe404(ps, res1)) {
			ctx.debug("Trailing slash probe looks like a 404, processing as a file");
			ps.getPath().setPathType(PathType.PATH_FILE);
		} else if(pcode  < 300 && rcode >= 300 && ps.getResponse().getBodyAsString().length() > 0) {
			ctx.debug("Trailing slash probe returned code 3xx - 5xx and initial fetch was a 200, processing as file");
			ps.getPath().setPathType(PathType.PATH_FILE);
		} else {
			ctx.debug("No idea what this is, try processing as file");
		}
		callFetchHandler(ctx, ps);
	}

	private boolean isProbe404(IPathState ps, IHttpResponse response) {
		final IPathState p404 = ps.get404Parent();
		final int rcode = response.getResponseCode();
		final IPageFingerprint rfp = response.getPageFingerprint();
		if(p404 != null)
			return p404.has404FingerprintMatching(rfp);
		else
			return rcode == 404;
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

	private void assumeDirectory(IHttpResponse response, IInjectionModuleContext ctx, IPathState ps) {
		ps.getPath().setPathType(PathType.PATH_DIRECTORY);
		ps.setUnknownFingerprint(ps.getPathFingerprint());
		ps.setResponse(response);
		callFetchHandler(ctx, ps);
	}

	private void callFetchHandler(IInjectionModuleContext ctx, IPathState ps) {
		final IWebPath path = ps.getPath();
		final HttpUriRequest req = ps.createRequest();
		final IHttpResponse res = ps.getResponse();
		if(path.getPathType() == PathType.PATH_DIRECTORY || path.getParentPath() == null)
			fetchDirProcessor.processResponse(null, req, res, ctx);
		else
			fetchFileProcessor.processResponse(null, req, res, ctx);
	}
	
	@Override
	public void processException(HttpUriRequest request, Throwable ex, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		ctx.reportRequestException(request, ex);
	}
}
