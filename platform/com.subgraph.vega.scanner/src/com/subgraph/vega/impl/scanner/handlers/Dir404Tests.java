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

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class Dir404Tests extends CrawlerModule {
	private static final String PAGE_DOES_NOT_EXIST = "nosuchpage123";

	private final DirParentCheck dirParentCheck = new DirParentCheck();
	private final CaseSensitivityCheck caseSensitivityCheck = new CaseSensitivityCheck();

	@Override
	public void initialize(IPathState ps) {
		final IInjectionModuleContext ctx = ps.createModuleContext();
		ctx.submitAlteredRequest(this, "/"+ PAGE_DOES_NOT_EXIST);
	}

	@Override
	public void runModule(HttpUriRequest request, IHttpResponse response, IInjectionModuleContext ctx) {
		final IPathState ps = ctx.getPathState();
		final boolean isFirstResponse = ctx.getCurrentIndex() == 0;
		boolean failed = false;

		if(ps.hasFailed404Detection())
			failed = true;

		if(response.isFetchFail()) {
			ctx.error(request, response, "during 404 response checks");
			failed = true;
		}

		if(!failed) {
			processResponseFingerprint(ctx, request, response, isFirstResponse);
			if(isFirstResponse)
				finishProcessingFirstResponse(ctx, ps);
		}

		finishProcessingResponse(ctx, ps);
	}

	private void finishProcessingResponse(IInjectionModuleContext ctx, IPathState ps) {
		ctx.incrementResponseCount();
		if(!ctx.allResponsesReceived())
			return;
		if(!ps.has404Fingerprints() || ps.hasFailed404Detection())
			handleFailed404Detection(ctx, ps);
		dirParentCheck.initialize(ps);
	}

	private void finishProcessingFirstResponse(IInjectionModuleContext ctx, IPathState ps) {
		if(!ps.has404Fingerprints()) {
			ctx.debug("First 404 probe failed to produce a signature");
		} else {
			ctx.pivotChecks(ps.createRequest(), ps.getResponse());
			caseSensitivityCheck.initialize(ps);
			scheduleProbes(ctx);
		}
	}

	private void processResponseFingerprint(IInjectionModuleContext ctx, HttpUriRequest req, IHttpResponse res, boolean isFirstResponse) {
		final IPageFingerprint fp = res.getPageFingerprint();
		final IPathState ps = ctx.getPathState();

		if(isFirstResponse && !ps.isSureDirectory() && !ps.isRootPath() && ps.matchesPathFingerprint(fp)) {
			ctx.debug("First 404 probe identical to parent page");
			return;
		}

		if(!ps.add404Fingerprint(fp)) {
			ctx.debug("Failed 404 detection, too many unique 404 signatures received");
			ps.setFailed404Detection();
			return;
		}

		final IPathState parent404 = ps.get404Parent();
		if((parent404 != null) && !parent404.has404FingerprintMatching(fp)) {
			ctx.debug("New 404 signature detected that was not detected on parent");
			ctx.responseChecks(req, res);
		}
	}

	private void scheduleProbes(IInjectionModuleContext ctx) {
		for(String ext: ctx.getFileExtensionList())
			ctx.submitAlteredRequest(this, PAGE_DOES_NOT_EXIST + "."+ ext, 1);
		ctx.submitAlteredRequest(this, "lpt9", 1);
		ctx.submitAlteredRequest(this, "~"+PAGE_DOES_NOT_EXIST, 1);
		ctx.submitAlteredRequest(this, PAGE_DOES_NOT_EXIST, 1);
	}

	private void handleFailed404Detection(IInjectionModuleContext ctx, IPathState ps) {
		final int code = (ps.getResponse() == null) ? (0) : (ps.getResponse().getResponseCode());
		if(code == 404) {
			ps.setPageMissing();
		} else if(code > 400) {
			logFailureMessage(ctx, code);
		} else if(!ps.isRootPath()) {
			ps.getPath().setPathType(PathType.PATH_PATHINFO);
		} else {
			ctx.debug("No distinctive 404 signatures detected");
		}
		ps.clear404Fingerprints();

		if(ps.getParentState() == null || ps.getParentState().getResponse() == null) {
			ctx.pivotChecks(ps.createRequest(), ps.getResponse());
			return;
		}

		if(!ps.getParentState().matchesPathFingerprint(ps.getPathFingerprint())) {
			IPathState parent404 = ps.get404Parent();
			if(parent404 == null || !parent404.has404FingerprintMatching(ps.getPathFingerprint())) {
				ctx.pivotChecks(ps.createRequest(), ps.getResponse());
			}
		}
	}

	private void logFailureMessage(IInjectionModuleContext ctx, int httpCode) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Directory resource is not accessible: HTTP response code (");
		sb.append(httpCode);
		sb.append(")");
		if(httpCode == 401)
			sb.append(" [HTTP Auth required");
		if(httpCode >= 500)
			sb.append(" [Server error]");
		ctx.debug(sb.toString());
	}


}
