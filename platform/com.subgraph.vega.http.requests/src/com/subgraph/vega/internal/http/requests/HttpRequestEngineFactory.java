package com.subgraph.vega.internal.http.requests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;

public class HttpRequestEngineFactory implements IHttpRequestEngineFactory {
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final HttpClient client = UnencodedHttpClientFactory.createHttpClient();
	private IHTMLParser htmlParser;
	
	@Override
	public IHttpRequestEngineConfig createConfig() {
		return new HttpRequestEngineConfig();
	}

	@Override
	public HttpClient createBasicClient() {
		return BasicHttpClientFactory.createHttpClient();
	}
	
	@Override
	public IHttpRequestEngine createRequestEngine(IHttpRequestEngineConfig config) {
		configureClient(client, config);
		return new HttpRequestEngine(executor, client, config, htmlParser);
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
