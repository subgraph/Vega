package com.subgraph.vega.api.crawler;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public interface ICrawlerResponseProcessor {
	void processResponse(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, Object argument);
}
