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
	 * Send the next request in the macro, returning the request task managing request execution. The provided
	 * HttpContext should use the request engine's HttpContext as its parent.
	 * @param context HTTP execution context.
	 * @return Request task, or null if no further items exist to execute.
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
	IHttpRequestTask sendNextRequest(HttpContext context) throws URISyntaxException, UnsupportedEncodingException;

	/**
  	 * Send the next request in the macro, returning the request task managing request execution. A HttpContext is
  	 * automatically generated for the request using the request engine's HttpContext as its parent. 
	 * @return Request task, or null if no further items exist to execute.
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
	IHttpRequestTask sendNextRequest() throws URISyntaxException, UnsupportedEncodingException;
}
