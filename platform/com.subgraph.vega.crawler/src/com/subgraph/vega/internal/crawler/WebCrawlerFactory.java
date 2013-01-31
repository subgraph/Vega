/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.crawler;

import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public class WebCrawlerFactory implements IWebCrawlerFactory {
	private final static int DEFAULT_RESPONSE_THREAD_COUNT = 20;
	private final static int MIN_REQUEST_THREAD_COUNT = 5;
	private final static int MAX_REQUEST_THREAD_COUNT = 100;
	private IHttpRequestEngineFactory requestEngineFactory;
	
	@Override
	public IWebCrawler create(IRequestOrigin requestOrigin) {
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(IHttpRequestEngine.EngineConfigType.CONFIG_SCANNER, requestEngineFactory.createConfig(), requestOrigin);
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
