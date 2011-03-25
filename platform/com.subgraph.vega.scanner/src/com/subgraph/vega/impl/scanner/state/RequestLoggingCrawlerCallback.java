package com.subgraph.vega.impl.scanner.state;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.requests.IRequestLog;

public class RequestLoggingCrawlerCallback implements ICrawlerResponseProcessor {

	private final IRequestLog requestLog;
	private final ICrawlerResponseProcessor callback;
	
	public RequestLoggingCrawlerCallback(IRequestLog requestLog, ICrawlerResponseProcessor callback) {
		this.requestLog = requestLog;
		this.callback = callback;
	}
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		requestLog.addRequestResponse(request, response.getRawResponse(), response.getHost());
		callback.processResponse(crawler, request, response, argument);
	}

}
