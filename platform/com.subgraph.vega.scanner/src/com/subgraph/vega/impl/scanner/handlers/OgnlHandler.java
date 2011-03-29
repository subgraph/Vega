package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IModuleContext;

public class OgnlHandler implements ICrawlerResponseProcessor {

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;

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

}
