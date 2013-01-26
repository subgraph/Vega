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

public abstract class CrawlerModule implements ICrawlerResponseProcessor {
	
	public abstract void initialize(IPathState ps);
	public abstract void runModule(HttpUriRequest request, IHttpResponse response, IInjectionModuleContext ctx); 

	public void processResponse(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, Object argument) {
		if(!(argument instanceof IInjectionModuleContext))
			throw new IllegalArgumentException("Crawler callback argument is not IModuleContext as expected: "+ argument);
		runModule(request, response, (IInjectionModuleContext) argument);
	}
	
	@Override
	public void processException(HttpUriRequest request, Throwable ex, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		ctx.reportRequestException(request, ex);
	}
}
