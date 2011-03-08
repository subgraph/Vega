package com.subgraph.vega.api.crawler;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

public interface IWebCrawlerFactory {
	IWebCrawler create();
	IWebCrawler create(IHttpRequestEngine requestEngine);
}
