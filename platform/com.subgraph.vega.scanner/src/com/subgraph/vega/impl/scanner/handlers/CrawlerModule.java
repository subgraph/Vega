package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public abstract class CrawlerModule implements ICrawlerResponseProcessor {
	
	public abstract void initialize(IPathState ps);
	public abstract void runModule(HttpUriRequest request, IHttpResponse response, IModuleContext ctx); 

	public void processResponse(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, Object argument) {
		if(!(argument instanceof IModuleContext))
			throw new IllegalArgumentException("Crawler callback argument is not IModuleContext as expected: "+ argument);
		runModule(request, response, (IModuleContext) argument);
	}

}
