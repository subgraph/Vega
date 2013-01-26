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

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class FileProcessor implements ICrawlerResponseProcessor {
	private final InjectionChecks injectionChecks = new InjectionChecks();
	private final SecondaryExtChecks secondaryExtChecks = new SecondaryExtChecks();
	private final ParametricCheckHandler parametricChecks = new ParametricCheckHandler();
	private final CaseSensitivityCheck caseCheck = new CaseSensitivityCheck();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		
		final IPathState ps = ctx.getPathState();
		ps.setResponse(response);
		ps.getPath().setVisited(true);

		if(response.isFetchFail()) {
			ctx.error(request, response, "during initial file fetch");
			return;
		}
		
		final IPathState ps404Parent = ps.get404Parent();
		boolean is404Response = ((ps404Parent == null) && response.getResponseCode() == 404) ||
			((ps404Parent != null) && ps404Parent.has404FingerprintMatching(response.getPageFingerprint()));
		
		if(is404Response) {
			ps.setPageMissing();
		} else {
			if(response.getResponseCode() > 400) 
				ctx.debug("Page is not accessible.  http code ("+ response.getResponseCode() + ")");
				
			final IPathState pps = ps.getParentState();
			if(pps == null || pps.getResponse() == null || !ps.matchesPathFingerprint(pps.getPathFingerprint())) {
				ctx.responseChecks(request, response);
			}
			if(ps404Parent != null && !ps.isParametric())
				secondaryExtChecks.initialize(ps);
			if(ps.getPath().getPathType() == PathType.PATH_FILE)
				caseCheck.initialize(ps);
				
		}
		
		ps.unlockChildren();
		if(ps.isParametric()) {
			parametricChecks.initialize(ps);
		} else {
			injectionChecks.initialize(ps);
		}
	}
	@Override
	public void processException(HttpUriRequest request, Throwable ex, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		ctx.reportRequestException(request, ex);
	}
}
