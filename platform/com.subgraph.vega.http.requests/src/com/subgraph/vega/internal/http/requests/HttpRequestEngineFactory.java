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
package com.subgraph.vega.internal.http.requests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.api.model.requests.IRequestOrigin;
import com.subgraph.vega.http.requests.builder.HttpRequestBuilder;
import com.subgraph.vega.http.requests.builder.HttpResponseBuilder;

public class HttpRequestEngineFactory implements IHttpRequestEngineFactory {
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private IHTMLParser htmlParser;
	private HttpHost proxy;

	@Override
	public void setProxy(HttpHost proxy) {
		this.proxy = proxy;
	}

	@Override
	public IHttpRequestEngineConfig createConfig() {
		return new HttpRequestEngineConfig();
	}

	@Override
	public IHttpRequestEngine createRequestEngine(IHttpRequestEngine.EngineConfigType type, IHttpRequestEngineConfig config, IRequestOrigin requestOrigin) {
		final HttpClient client = BasicHttpClientFactory.createHttpClient(type);
		if(proxy != null) {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		configureClient(client, config);
		return new HttpRequestEngine(type, executor, client, config, requestOrigin, htmlParser);
	}
	
	@Override
	public IHttpRequestBuilder createRequestBuilder() {
		return new HttpRequestBuilder();
	}

	@Override
	public IHttpResponseBuilder createResponseBuilder() {
		return new HttpResponseBuilder();
	}

	private void configureClient(HttpClient client, IHttpRequestEngineConfig config) {
		final ClientConnectionManager connectionManager = client.getConnectionManager();
		if(connectionManager instanceof PoolingClientConnectionManager) {
			PoolingClientConnectionManager ccm = (PoolingClientConnectionManager) connectionManager;
			ccm.setMaxTotal(config.getMaxConnections());
			ccm.setDefaultMaxPerRoute(config.getMaxConnectionsPerRoute());
		}
	}

	protected void setHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = htmlParser;
	}
	
	protected void unsetHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = null;
	}

}
