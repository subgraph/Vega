package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;

public class InjectHandler4 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler5 = new InjectHandler5();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		
		ctx.contentChecks(request, response);
		
		if(ctx.getCurrentIndex() > 0)
			return;

		final IModuleContext newCtx = ctx.getPathState().createModuleContext();
		String injectme[] = { "http://vega.invalid/;?", "//vega.invalid/;?",  "vega://invalid/;?"};
		newCtx.submitMultipleAlteredRequests(injectHandler5, injectme);
	}

}
