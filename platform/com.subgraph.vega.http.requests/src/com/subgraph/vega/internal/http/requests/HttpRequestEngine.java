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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestModifier;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestEngineException;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public class HttpRequestEngine implements IHttpRequestEngine {
	public final static String VEGA_SENT_REQUEST = "vega.sent-request"; /** Key under which a copy of sent request with actual sent headers is stored in HttpContext */
	private final Logger logger = Logger.getLogger("request-engine");
	private final ExecutorService executor;
	private final HttpClient client;
	private final IHttpRequestEngineConfig config;
	private final IRequestOrigin requestOrigin;
	private final IHTMLParser htmlParser;
	private final RateLimiter rateLimit;
	private final List<IHttpRequestModifier> requestModifierList;

	HttpRequestEngine(ExecutorService executor, HttpClient client, IHttpRequestEngineConfig config, IRequestOrigin requestOrigin, IHTMLParser htmlParser) {
		this.executor = executor;
		this.client = client;
		this.config = config;
		this.requestOrigin = requestOrigin;
		this.htmlParser = htmlParser;
		rateLimit = new RateLimiter(config.getRequestsPerMinute());
		requestModifierList = new ArrayList<IHttpRequestModifier>();
		addRequestModifier(new HttpRequestModifierCookies(this.config));
	}

	@Override
	public IHttpRequestEngineConfig getRequestEngineConfig() {
		return config;
	}

	@Override
	public IRequestOrigin getRequestOrigin() {
		return requestOrigin;
	}

	@Override
	public HttpClient getHttpClient() {
		return client;
	}

	@Override
	public void addRequestModifier(IHttpRequestModifier modifier) {
		requestModifierList.add(modifier);
	}

	@Override
	public IHttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws RequestEngineException {
		final HttpContext requestContext = (context == null) ? (new BasicHttpContext()) : (context);
		for (IHttpRequestModifier modifier: requestModifierList) {
			modifier.process(request, requestContext);
		}
		Future<IHttpResponse> future = executor.submit(new RequestTask(client, rateLimit, request, requestOrigin, requestContext, config, htmlParser));
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
