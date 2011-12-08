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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpMacroContext;
import com.subgraph.vega.api.http.requests.IHttpMacroExecutor;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestModifier;
import com.subgraph.vega.api.http.requests.IHttpRequestTask;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestTaskStartEvent;
import com.subgraph.vega.api.http.requests.RequestTaskStopEvent;
import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public class HttpRequestEngine implements IHttpRequestEngine {
	public final static String VEGA_SENT_REQUEST = "vega.sent-request"; /** Key under which a copy of sent request with actual sent headers is stored in HttpContext */
	private final ExecutorService executor;
	private final HttpClient client;
	private final IHttpRequestEngineConfig config;
	private final IRequestOrigin requestOrigin;
	private final IHTMLParser htmlParser;
	private final RateLimiter rateLimit;
	private final HttpContext httpContext;
	private final List<IHttpRequestModifier> requestModifierList;
	private final EventListenerManager requestEventManager;
	private final List<HttpRequestTask> requestInProgressList;
	
	HttpRequestEngine(ExecutorService executor, HttpClient client, IHttpRequestEngineConfig config, IRequestOrigin requestOrigin, IHTMLParser htmlParser) {
		this.executor = executor;
		this.client = client;
		this.config = config;
		this.requestOrigin = requestOrigin;
		this.htmlParser = htmlParser;
		rateLimit = new RateLimiter(config.getRequestsPerMinute());
		httpContext = new SyncBasicHttpContext(null);
		httpContext.setAttribute(ClientContext.COOKIE_STORE, config.getCookieStore());
		requestModifierList = new ArrayList<IHttpRequestModifier>();
		requestEventManager = new EventListenerManager();
		requestInProgressList = new ArrayList<HttpRequestTask>();
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
	public HttpContext getHttpContext() {
		return httpContext;
	}

	@Override
	public void addRequestModifier(IHttpRequestModifier modifier) {
		requestModifierList.add(modifier);
	}

	@Override
	public void addRequestListener(IEventHandler listener) {
		requestEventManager.addListener(listener);
	}

	@Override
	public void removeRequestListener(IEventHandler listener) {
		requestEventManager.removeListener(listener);
	}

	@Override
	public IHttpRequestTask[] getRequestList() {
		synchronized(this) {
			return requestInProgressList.toArray(new IHttpRequestTask[0]);
		}
	}
	
	@Override
	public IHttpRequestTask sendRequest(HttpUriRequest request, HttpContext context) {
		for (IHttpRequestModifier modifier: requestModifierList) {
			modifier.process(request, context);
		}
		HttpRequestTask requestTask = new HttpRequestTask(this, client, rateLimit, request, requestOrigin, context, config, htmlParser);
		Future<IHttpResponse> future = executor.submit(requestTask);
		requestTask.setFuture(future);
		return requestTask;
	}

	@Override
	public IHttpRequestTask sendRequest(HttpUriRequest request) {
		return sendRequest(request, new BasicHttpContext(httpContext));
	}
	
	@Override
	public IHttpMacroContext createMacroContext() {
		return new HttpMacroContext();
	}

	@Override
	public IHttpMacroExecutor createMacroExecutor(IHttpMacro macro, IHttpMacroContext context) {
		return new HttpMacroExecutor(this, macro, context);
	}

	public void addRequestInProgress(HttpRequestTask requestTask) {
		synchronized(this) {
			requestInProgressList.add(requestTask);
			requestEventManager.fireEvent(new RequestTaskStartEvent(requestTask));
		}
	}
	
	public void removeRequestInProgress(HttpRequestTask requestTask) {
		synchronized(this) {
			requestInProgressList.remove(requestTask);
			requestEventManager.fireEvent(new RequestTaskStopEvent(requestTask));
		}
	}
	
}
