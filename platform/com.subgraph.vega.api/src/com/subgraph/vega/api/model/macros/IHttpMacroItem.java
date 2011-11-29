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

import java.util.Collection;

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
	 * Get a list of request parameters.
	 * @return Request parameters. 
	 */
	Collection<IHttpMacroItemParam> getParams();

	/**
	 * Get a request parameter by name.
	 * @param name Parameter name.
	 * @return Parameter, or null if none exists for that name.
	 */
	IHttpMacroItemParam getParam(String name);
}
