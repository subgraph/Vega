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
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;

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
	public HttpClient createBasicClient() {
		HttpClient client = BasicHttpClientFactory.createHttpClient(); 
		if (proxy != null) {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return client;
	}
	
	@Override
	public HttpClient createUnencodingClient() {
		HttpClient client = UnencodedHttpClientFactory.createHttpClient();
		if (proxy != null) {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return client;
	}

	@Override
	public IHttpRequestEngine createRequestEngine(HttpClient client, IHttpRequestEngineConfig config) {
		configureClient(client, config);
		return new HttpRequestEngine(executor, client, config, htmlParser);
	}
	
	private void configureClient(HttpClient client, IHttpRequestEngineConfig config) {
		final ClientConnectionManager connectionManager = client.getConnectionManager();
		if(connectionManager instanceof ThreadSafeClientConnManager) {
			ThreadSafeClientConnManager ccm = (ThreadSafeClientConnManager) connectionManager;
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
