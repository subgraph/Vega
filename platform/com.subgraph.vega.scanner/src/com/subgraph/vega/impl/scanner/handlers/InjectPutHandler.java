package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IModuleContext;

public class InjectPutHandler implements ICrawlerResponseProcessor {

	private final InjectionChecks injectionChecks;
	
	InjectPutHandler(InjectionChecks injectionChecks) {
		this.injectionChecks = injectionChecks;
	}
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "during PUT checks");
		} else {
			final int rc = response.getResponseCode();
			final IPageFingerprint fp = response.getPageFingerprint();
			
			if(rc >= 200 && rc < 300 && !fp.isSame(ctx.getPathState().getPathFingerprint())) {
				System.out.println("PUT directory");
			}
		}
		injectionChecks.initialize2(ctx.getPathState());
	}

}
