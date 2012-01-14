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
package com.subgraph.vega.api.model.macros;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpMacroContext;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam.ValueSetIn;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public interface IHttpMacroItem {
	/**
	 * Get the IRequestLogRecord this macro is derived from.
	 * @return IRequestLogRecord.
	 */
	IRequestLogRecord getRequestLogRecord();

	/**
	 * Set whether cookies set in the request engine should be used when sending the macro request. 
	 * @param useCookies Boolean value indicating whether cookies should be used.
	 */
	void setUseCookies(boolean useCookies);

	/**
	 * Returns a boolean value indicating whether cookies set in the request engine should be used when sending the
	 * macro request.
	 * @return Boolean value Boolean value indicating whether cookies should be used.
	 */
	boolean getUseCookies();
	
	/**
	 * Set whether cookies from the response should be kept in the request engine.
	 * @param keepCookies Booelan value indicating whether cookies from the response should be kept. 
	 */
	void setKeepCookies(boolean keepCookies);

	/**
	 * Returns a boolean value indicating whether cookies received in the response to the macro should be set in the
	 * request engine.
	 * @return Booelan value indicating whether cooies from the response should be kept,
	 */
	boolean getKeepCookies();

	/**
	 * Create an IHttpMacroItemParam and add it to this macro item.
	 * @param name Parameter name.
	 * @param value Parameter value, or null.
	 * @param setIn Where the parameter is set.
	 * @return IHttpMacroItemParam.
	 */
	IHttpMacroItemParam createParam(String name, String value, ValueSetIn setIn);

	/**
	 * Remove a parameter.
	 * @param param Parameter.
	 */
	void removeParam(IHttpMacroItemParam param);
	
	/**
	 * Returns the number of parameters in this macro.
	 * @return The number of parameters.
	 */
	int paramsSize();

	/**
	 * Get the array index of a parameter.
	 * @param param Parameter.
	 * @return Array index, or -1.
	 */
	int indexOfParam(IHttpMacroItemParam param);
	
	/**
	 * Swap parameters at two positions.
	 * @return Swapped parameters. 
	 */
	void swapParams(int idx1, int idx2);

	/**
	 * Get a list of request parameters.
	 * @return Request parameters. 
	 */
	Collection<IHttpMacroItemParam> getParams();

	/**
	 * Get any request parameters for a parameter name.
	 * @param name Parameter name.
	 * @return Array of parameters for the name.
	 */
	IHttpMacroItemParam[] getParam(String name);

	/**
	 * Create a request for this macro item.
	 * @param requestBuilder Request builder.
	 * @throws UnsupportedEncodingException 
	 * @throws URISyntaxException 
	 */
	HttpUriRequest createRequest(IHttpMacroContext context) throws URISyntaxException, UnsupportedEncodingException;
	
	/**
	 * Set the contents of a request for this macro item within a request builder. Any existing message properties that
	 * would interfere with the meaning of the request builder are removed. 
	 * @param requestBuilder Request builder.
	 * @param context Macro context.
	 * @throws UnsupportedEncodingException 
	 * @throws URISyntaxException 
	 */
	void setRequestBuilder(IHttpRequestBuilder requestBuilder, IHttpMacroContext context) throws UnsupportedEncodingException, URISyntaxException;

	/**
	 * Update the macro request from a request builder. 
	 * @param requestBuilder Request builder.
	 * @throws URISyntaxException 
	 */
	void updateFromRequestBuilder(IHttpRequestBuilder requestBuilder) throws URISyntaxException;
	
}
