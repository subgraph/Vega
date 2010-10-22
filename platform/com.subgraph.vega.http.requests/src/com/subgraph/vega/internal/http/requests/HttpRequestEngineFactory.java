package com.subgraph.vega.internal.http.requests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpClient;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;

public class HttpRequestEngineFactory implements IHttpRequestEngineFactory {
	private final static int NTHREADS = 12;
	private final ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
	private final HttpClient client = HttpClientFactory.createHttpClient();
	private IHTMLParser htmlParser;
	private IModel model;
	
	@Override
	public IHttpRequestEngineConfig createConfig() {
		return new HttpRequestEngineConfig();
	}

	@Override
	public IHttpRequestEngine createRequestEngine(
			IHttpRequestEngineConfig config) {
		return new HttpRequestEngine(executor, client, config, htmlParser, model.getCurrentWorkspace().getRequestLog());
	}
	
	protected void setHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = htmlParser;
	}
	
	protected void unsetHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = null;
	}
	
	protected void setModel(IModel model) {
		this.model = model;
	}
	
	protected void unsetModel(IModel model) {
		this.model = null;
	}
	

}
