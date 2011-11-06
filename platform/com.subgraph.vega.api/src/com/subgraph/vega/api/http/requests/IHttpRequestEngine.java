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
	 * Register a request modifier.
	 * @param modifier IHttpRequestModifier.
	 */
	void addRequestModifier(IHttpRequestModifier modifier);
	
	/**
	 * Send a request, specifying the HTTP context. 
	 * 
	 * The following execution context variables cannot be set and will be overwritten:
	 * 	- ClientContext.COOKIE_STORE (set in the IHttpRequestEngineConfig this request engine was instantiated with)
	 * 
	 * Additional variables may be overwritten by request modifiers.
	 * 
	 * @param request Request to send.
	 * @param context HTTP context.
	 * @return IHttpResponse
	 * @throws RequestEngineException
	 */
	IHttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws RequestEngineException;

	/**
	 * Send a request, specifying the HTTP context.
	 * 
	 * @param request Request to send.
	 * @return IHttpResponse
	 * @throws RequestEngineException
	 */
	IHttpResponse sendRequest(HttpUriRequest request) throws RequestEngineException;
}
