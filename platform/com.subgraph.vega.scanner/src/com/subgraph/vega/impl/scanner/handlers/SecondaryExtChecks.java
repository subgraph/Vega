package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IPathState;

public class SecondaryExtChecks implements ICrawlerResponseProcessor {

	public void initialize() {
		
	}
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		
		
		// TODO Auto-generated method stub
		
	}
	
	

}
