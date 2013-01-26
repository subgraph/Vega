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
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;

public class OgnlHandler implements ICrawlerResponseProcessor {

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;

		if(ctx.hasModuleFailed())
			return;
		
		if(response.isFetchFail()) {
			ctx.setModuleFailed();
			ctx.error(request, response, "during OGNL tests");
			return;
		}
		
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		if(ctx.incrementResponseCount() < 2)
			return;
		final IPageFingerprint pathFP = ctx.getPathState().getPathFingerprint();
		
		if(ctx.isFingerprintMatch(0, pathFP) && ! ctx.isFingerprintMatch(1, pathFP)) {
			System.out.println("Problem: response to [0]['name']=... identical to name=...");			
		}
	}

	@Override
	public void processException(HttpUriRequest request, Throwable ex, Object argument) {
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		ctx.reportRequestException(request, ex);
	}
}
