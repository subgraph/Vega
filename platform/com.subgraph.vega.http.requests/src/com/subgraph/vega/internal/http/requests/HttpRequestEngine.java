package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;


public class HttpRequestEngine implements IHttpRequestEngine {
	private final Logger logger = Logger.getLogger("request-engine");
	private final ExecutorService executor;
	private final VegaHttpClient client;
	private final IHttpRequestEngineConfig config;
	private final IHTMLParser htmlParser;
	private final RateLimiter rateLimit;

	HttpRequestEngine(ExecutorService executor, VegaHttpClient client, IHttpRequestEngineConfig config, IHTMLParser htmlParser) {
		this.executor = executor;
		this.client = client;
		this.config = config;
		this.htmlParser = htmlParser;
		this.rateLimit = new RateLimiter(config.getRequestsPerMinute());
		
	}
	
	@Override
	public IHttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws IOException {
		final HttpContext requestContext = (context == null) ? (new BasicHttpContext()) : (context);
		Future<IHttpResponse> future = executor.submit(new RequestTask(client, rateLimit, request, requestContext, config, htmlParser));
		try {
			return future.get();
		} catch (InterruptedException e) {
			logger.info("Request "+ request.getURI() +" was interrupted before completion");
		} catch (ExecutionException e) {
			if(e.getCause() instanceof IOException) 
				throw ((IOException)e.getCause());
			logger.log(Level.WARNING, "Unexpected exception processing request "+ request.getURI() +" : "+ e.getCause().getMessage(), e.getCause());
		}
		return null;
	}

	public IHttpResponse sendRequest(HttpUriRequest request)
			throws IOException, ClientProtocolException {
		return sendRequest(request, null);
	}

	@Override
	public void registerResponseProcessor(IHttpResponseProcessor processor) {
		config.registerResponseProcessor(processor);		
	}

	@Override
	public IHttpRequestBuilder createRequestBuilder() {
		return new HttpRequestBuilder();
	}
}
