package com.subgraph.vega.api.crawler;

import java.net.URI;

public interface IWebCrawlerFactory {
	IWebCrawler create(URI baseURI);
}
