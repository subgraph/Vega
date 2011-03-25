package com.subgraph.vega.internal.crawler;

import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;

public class WebCrawlerFactory implements IWebCrawlerFactory {

	private IHttpRequestEngineFactory requestEngineFactory;
	
	@Override
	public IWebCrawler create() {
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		return create(requestEngine);
	}

	@Override
	public IWebCrawler create(IHttpRequestEngine requestEngine) {
		return new WebCrawler(requestEngine);
	}

	protected void setRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = factory;
	}
	
	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
	}
}
