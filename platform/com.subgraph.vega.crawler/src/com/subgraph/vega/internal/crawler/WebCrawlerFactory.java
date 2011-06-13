package com.subgraph.vega.internal.crawler;

import org.apache.http.client.HttpClient;

import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;

public class WebCrawlerFactory implements IWebCrawlerFactory {
	private final static int DEFAULT_RESPONSE_THREAD_COUNT = 10;
	private final static int MIN_REQUEST_THREAD_COUNT = 5;
	private final static int MAX_REQUEST_THREAD_COUNT = 100;
	private IHttpRequestEngineFactory requestEngineFactory;
	
	@Override
	public IWebCrawler create() {
		final HttpClient client = requestEngineFactory.createBasicClient();
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(client, requestEngineFactory.createConfig());
		return create(requestEngine);
	}

	@Override
	public IWebCrawler create(IHttpRequestEngine requestEngine) {
		return new WebCrawler(requestEngine, getRequestThreadCount(requestEngine), DEFAULT_RESPONSE_THREAD_COUNT);
	}

	protected void setRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = factory;
	}
	
	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
	}
	
	private int getRequestThreadCount(IHttpRequestEngine requestEngine) {
		int connectLimit = requestEngine.getRequestEngineConfig().getMaxConnections();
		if(connectLimit < MIN_REQUEST_THREAD_COUNT) {
			return MIN_REQUEST_THREAD_COUNT;
		} else if(connectLimit > MAX_REQUEST_THREAD_COUNT) {
			return MAX_REQUEST_THREAD_COUNT;
		} else {
			return connectLimit;
		}
	}
}
