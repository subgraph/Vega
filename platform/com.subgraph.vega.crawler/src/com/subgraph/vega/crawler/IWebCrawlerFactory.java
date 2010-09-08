package com.subgraph.vega.crawler;

import java.net.URI;

public interface IWebCrawlerFactory {
	IWebCrawler create(URI baseURI);
}
