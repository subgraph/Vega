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
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class DirectoryProcessor implements ICrawlerResponseProcessor {

	private final Dir404Tests dir404Tests;
	private final SecondaryExtChecks secondaryExt;

	public DirectoryProcessor() {
		this.dir404Tests = new Dir404Tests();
		this.secondaryExt = new SecondaryExtChecks();
	}

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		ps.getPath().setVisited(true);

		ps.setResponse(response);

		if(ps.isRootPath())
			ctx.pivotChecks(request, response);

		dir404Tests.initialize(ps);

		if(ps.get404Parent() != null)
			secondaryExt.initialize(ps);
	}

	@Override
	public void processException(HttpUriRequest request, Throwable ex, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		ctx.reportRequestException(request, ex);
	}
}
