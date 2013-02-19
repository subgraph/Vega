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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
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
import com.subgraph.vega.http.requests.custom.IEncodableHttpRequest;
import com.subgraph.vega.http.requests.custom.VegaHttpEntityEnclosingUriRequest;
import com.subgraph.vega.http.requests.custom.VegaHttpUriRequest;
import com.subgraph.vega.internal.http.requests.client.VegaDecompressingHttpClient;
import com.subgraph.vega.internal.http.requests.config.IRequestEncodingStrategy;
import com.subgraph.vega.internal.http.requests.config.RequestEngineConfig;

public class HttpRequestEngine implements IHttpRequestEngine {
	public final static String VEGA_SENT_REQUEST = "vega.sent-request"; /** Key under which a copy of sent request with actual sent headers is stored in HttpContext */
	private final static boolean isDecompressingClient = true;
	private final IRequestEncodingStrategy encodingStrategy;
	private final ExecutorService executor;
	private final HttpClient client;
	private final HttpClient decompressingClient;
	private final IHttpRequestEngineConfig config;
	private final IRequestOrigin requestOrigin;
	private final IHTMLParser htmlParser;
	private final RateLimiter rateLimit;
	private final CookieMatcher cookieMatcher;
	private final HttpContext httpContext;
	private final List<IHttpRequestModifier> requestModifierList;
	private final EventListenerManager requestEventManager;
	private final List<HttpRequestTask> requestInProgressList;
	
	HttpRequestEngine(EngineConfigType type, ExecutorService executor, HttpClient client, IHttpRequestEngineConfig config, IRequestOrigin requestOrigin, IHTMLParser htmlParser) {
		this.encodingStrategy = RequestEngineConfig.getRequestEncodingStrategy(type);
		this.executor = executor;
		this.client = client;
		this.decompressingClient = new VegaDecompressingHttpClient(client);
		this.config = config;
		this.requestOrigin = requestOrigin;
		this.htmlParser = htmlParser;
		rateLimit = new RateLimiter(config.getRequestsPerMinute());
		cookieMatcher = new CookieMatcher(getClientDowncast());
		httpContext = new SyncBasicHttpContext(null);

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
	public CookieStore getCookieStore() {
		return getClientDowncast().getCookieStore();
	}

	@Override
	public void setCookieStore(CookieStore cookieStore) {
		getClientDowncast().setCookieStore(cookieStore);
	}

	private AbstractHttpClient getClientDowncast() {
		if(client instanceof AbstractHttpClient) {
			return (AbstractHttpClient)client;
		}
		throw new IllegalArgumentException("HttpClient instance is not expected type");
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
		if(request instanceof IEncodableHttpRequest) {
			((IEncodableHttpRequest) request).encodeWith(encodingStrategy);
		}
		for (IHttpRequestModifier modifier: requestModifierList) {
			modifier.process(request, context);
		}
		
		HttpRequestTask requestTask = new HttpRequestTask(
				this,
				(isDecompressingClient) ? (decompressingClient) : (client),
				rateLimit, 
				request, 
				requestOrigin, 
				context, 
				config, 
				htmlParser);
		
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

	@Override
	public HttpUriRequest createGetRequest(HttpHost target, String uri) {
		return new VegaHttpUriRequest(target, HttpGet.METHOD_NAME, uri);
	}

	@Override
	public HttpUriRequest createPostRequest(HttpHost target, String uri) {
		return new VegaHttpEntityEnclosingUriRequest(target, HttpPost.METHOD_NAME, uri);
	}

	@Override
	public HttpUriRequest createRawRequest(HttpHost target,
			RequestLine requestLine) {
		return new VegaHttpUriRequest(target, requestLine);
	}

	@Override
	public HttpUriRequest createRawEntityEnclosingRequest(HttpHost target,
			RequestLine requestLine) {
		return new VegaHttpEntityEnclosingUriRequest(target, requestLine);
	}

	@Override
	public List<Cookie> getCookiesForRequest(HttpHost targetHost,
			HttpRequest request) {
		return cookieMatcher.getCookiesForRequest(targetHost, request);
	}
}
