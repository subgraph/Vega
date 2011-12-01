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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public interface IHttpRequestEngine {
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
	 * Register a request modifier.
	 * @param modifier IHttpRequestModifier.
	 */
	void addRequestModifier(IHttpRequestModifier modifier);
	
	/**
	 * Send a request, specifying the HttpContext. The HttpContext should use this request engine's HttpContext as its
	 * parent. 
	 * @param request Request to send.
	 * @param context HTTP context.
	 * @return IHttpResponse Response.
	 * @throws RequestEngineException
	 */
	IHttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws RequestEngineException;

	/**
	 * Send a request without providing a HttpContext. A HttpContext is automatically generated for the request with
	 * this request engine's HttpContext as its parent.
	 * @param request Request to send.
	 * @return IHttpResponse Response.
	 * @throws RequestEngineException
	 */
	IHttpResponse sendRequest(HttpUriRequest request) throws RequestEngineException;

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
	
}
