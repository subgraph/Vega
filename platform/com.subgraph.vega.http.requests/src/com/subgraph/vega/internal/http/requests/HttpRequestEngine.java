package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.api.http.requests.RequestEngineException;

public class HttpRequestEngine implements IHttpRequestEngine {
	public final static String VEGA_SENT_REQUEST = "vega.sent-request"; /** Key under which a copy of sent request with actual sent headers is stored in HttpContext */
	private final Logger logger = Logger.getLogger("request-engine");
	private final ExecutorService executor;
	private final HttpClient client;
	private final IHttpRequestEngineConfig config;
	private final IHTMLParser htmlParser;
	private final RateLimiter rateLimit;

	HttpRequestEngine(ExecutorService executor, HttpClient client, IHttpRequestEngineConfig config, IHTMLParser htmlParser) {
		this.executor = executor;
		this.client = client;
		this.config = config;
		this.htmlParser = htmlParser;
		this.rateLimit = new RateLimiter(config.getRequestsPerMinute());
	}

	@Override
	public IHttpRequestEngineConfig getRequestEngineConfig() {
		return config;
	}

	@Override
	public IHttpRequestBuilder createRequestBuilder() {
		return new HttpRequestBuilder();
	}

	@Override
	public IHttpResponseBuilder createResponseBuilder() {
		return new HttpResponseBuilder();
	}

	@Override
	public IHttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws RequestEngineException {
		final HttpContext requestContext = (context == null) ? (new BasicHttpContext()) : (context);
		requestContext.setAttribute(ClientContext.COOKIE_STORE, config.getCookieStore());
		Future<IHttpResponse> future = executor.submit(new RequestTask(client, rateLimit, request, requestContext, config, htmlParser));
		try {
			return future.get();
		} catch (InterruptedException e) {
			logger.info("Request "+ request.getURI() +" was interrupted before completion");
		} catch (ExecutionException e) {
			throw translateException(request, e.getCause());
		}
		return null;
	}

	public IHttpResponse sendRequest(HttpUriRequest request) throws RequestEngineException {
		return sendRequest(request, null);
	}

	private RequestEngineException translateException(HttpUriRequest request, Throwable ex) {
		final StringBuilder sb = new StringBuilder();

		if(ex instanceof IOException) {
			sb.append("Network problem");
		} else if(ex instanceof HttpException) {
			sb.append("Protocol problem");
		} else {
			sb.append("Unknown problem");
		}
		sb.append(" while retrieving URI ");
		sb.append(request.getURI().toString());
		sb.append(" [");
		sb.append(ex.getMessage());
		sb.append("]");
		return new RequestEngineException(sb.toString(), ex);
	}
}
