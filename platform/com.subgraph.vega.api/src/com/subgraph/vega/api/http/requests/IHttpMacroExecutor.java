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

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.model.macros.IHttpMacro;

/**
 * Manages execution of a HTTP macro.
 */
public interface IHttpMacroExecutor {
	/**
	 * Get the request engine used with the macro executor.
	 * @return Request engine.
	 */
	IHttpRequestEngine getRequestEngine();

	/**
	 * Get the macro.
	 * @return Macro.
	 */
	IHttpMacro getMacro();

	/**
	 * Get the HTTP macro context.
	 * @return Macro context.
	 */
	IHttpMacroContext getMacroContext();
	
	/**
	 * Determine whether the macro has additional items to execute.
	 * @return Boolean indicating whether the macro has additional items to execute.
	 */
	boolean hasNext();

	/**
	 * Send the next request in the macro. The HttpContext should use the request engine's HttpContext as its parent.
	 * @param context HTTP context.
	 * @return IHttpResponse response, or null if no further items exist to execute.
	 * @throws RequestEngineException
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
	IHttpResponse sendNextRequest(HttpContext context) throws RequestEngineException, URISyntaxException, UnsupportedEncodingException;

	/**
	 * Send the next request in the macro without providing a HttpContext. A HttpContext is automatically generated for
	 * the request with the request engine's HttpContext as its parent.
	 * @return IHttpResponse response, or null if no further items exist to execute.
	 * @throws RequestEngineException
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
	IHttpResponse sendNextRequest() throws RequestEngineException, URISyntaxException, UnsupportedEncodingException;
}
