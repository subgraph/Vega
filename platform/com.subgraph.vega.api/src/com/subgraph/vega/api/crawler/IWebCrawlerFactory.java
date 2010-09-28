package com.subgraph.vega.api.crawler;

import java.net.URI;


public interface IWebCrawlerFactory {
	IWebCrawler create(ICrawlerConfig config);
	ICrawlerConfig createBasicConfig(URI baseURI);
}
