package com.subgraph.vega.api.crawler;

import java.net.URI;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;


public interface IWebCrawlerFactory {
	IWebCrawler create(ICrawlerConfig config);
	IWebCrawler create(ICrawlerConfig config, IHttpRequestEngine requestEngine);
	ICrawlerConfig createBasicConfig(URI baseURI);
}
