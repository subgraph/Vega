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
package com.subgraph.vega.api.http.requests;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public interface IHttpRequestEngine {
	enum EngineConfigType { CONFIG_SCANNER, CONFIG_PROXY };
	/**
	 * Get the configuration for this request engine.
	 * 
	 * @return IHttpRequestEngineConfig
	 */
	IHttpRequestEngineConfig getRequestEngineConfig();

	/**
	 * Get the request origin associated with this request engine.
	 * @return Request origin.
	 */
	IRequestOrigin getRequestOrigin();

	/**
	 * Get the HttpClient used by this request engine.
	 * @return HttpClient.
	 */
	HttpClient getHttpClient();
	
	/**
	 * Get the parent HttpContext associated with this request engine. The parent HttpContext is thread-safe and should
	 * be used as the parent for the request HttpContext.
	 * @return Parent HttpContext.
	 */
	HttpContext getHttpContext();
	
	/**
	 * Get the cookie store from the HttpClient instance used by this request engine
	 * 
	 * @return CookieStore instance from HttpClient.
	 */
	CookieStore getCookieStore();
	
	/**
	 * Replace the cookie store for the HttpClient instance used by this request engine.
	 * 
	 * @param cookieStore The new CookieStore to use.
	 */
	void setCookieStore(CookieStore cookieStore);
	
	/**
	 * Returns the cookies which would be applied to the specified request before transmitting it
	 * to the given host.  This method does not modify the request, it only searches the cookie store
	 * for the cookies which would be added to the request before transmission and returns them.
	 * 
	 * @param targetHost The host this request will be sent to.
	 * @param request The request which the cookies would be added to.
	 * @return The list of cookies which would be added to this request upon transmission.
	 */
	List<Cookie> getCookiesForRequest(HttpHost targetHost, HttpRequest request);
	
	/**
	 * Register a request modifier.
	 * @param modifier IHttpRequestModifier.
	 */
	void addRequestModifier(IHttpRequestModifier modifier);

	/**
	 * Register an event listener to watch for requests as they are executed by this request engine. Fires:
	 * 	- RequestTaskStartEvent
	 *  - RequestTaskStopEvent
	 * @param listener Event listener.
	 */
	void addRequestListener(IEventHandler listener);

	/**
	 * Deregister a request event listener.
	 * @param listener Event listener.
	 */
	void removeRequestListener(IEventHandler listener);

	/**
	 * Obtain a list of requests in progress.
	 * @return List of requests in progress.
	 */
	IHttpRequestTask[] getRequestList();

	/**
	 * Send a request, returning the request task managing request execution. The provided HttpContext should use this
	 * request engine's HttpContext as its parent.
	 * @param request Request to be send.
	 * @param context HTTP execution context.
	 * @return Request task.
	 */
	IHttpRequestTask sendRequest(HttpUriRequest request, HttpContext context);

	/**
	 * Send a request, returning the request task managing request execution. A HttpContext is automatically generated
	 * for the request using this request engine's HttpContext as its parent.
	 * @param request Request to be send.
	 * @return Request task.
	 */
	IHttpRequestTask sendRequest(HttpUriRequest request);
	
	/**
	 * Create a macro context.
	 * @return Macro context.
	 */
	IHttpMacroContext createMacroContext();
	
	/**
	 * Create a macro executor to execute a macro with this request engine.
	 * @param macro Macro to execute.
	 * @param context Macro context.
	 * @return Macro executor.
	 */
	IHttpMacroExecutor createMacroExecutor(IHttpMacro macro, IHttpMacroContext context);
	
	HttpUriRequest createGetRequest(HttpHost target, String uri);
	HttpUriRequest createPostRequest(HttpHost target, String uri);
	HttpUriRequest createRawRequest(HttpHost target, RequestLine requestLine);
	HttpUriRequest createRawEntityEnclosingRequest(HttpHost target, RequestLine requestLine);
}
