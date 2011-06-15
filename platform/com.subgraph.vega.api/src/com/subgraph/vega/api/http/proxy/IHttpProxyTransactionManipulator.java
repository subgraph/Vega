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
package com.subgraph.vega.api.http.proxy;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * Interface to set properties of request and response manipulation to be performed by the proxy. Transactions are
 * manipulated before being passed through the interceptor.
 *
 * Future plans: support addition and removal of headers, match and replace of headers and entity bodies, etc.
 */
public interface IHttpProxyTransactionManipulator {
	/**
	 * Set the content of the User-Agent header sent in a client request. 
	 * 
	 * @param userAgent Content of the User-Agent header, or null to disable.
	 */
	void setUserAgent(String userAgent);

	/**
	 * Set whether to always override the client User-Agent header.
	 * 
	 * @param override Boolean value indicating whether to override the client User-Agent header.
	 */
	void setUserAgentOverride(boolean override);
	
	/**
	 * Set whether to prevent browser caching by manipulating related headers. 
	 * 
	 * @param disable Boolean value indicating whether to disable browser caching.  
	 */
	void setBrowserCacheDisable(boolean disable);

	/**
	 * Set whether to prevent intermediate (proxy) caching by manipulating related headers. 
	 * 
	 * @param disable Boolean value indicating whether to disable intermediate caching.  
	 */
	void setProxyCacheDisable(boolean disable);

	/**
	 * Manipulate a HTTP request.
	 * 
	 * @param request HttpRequest.
	 */
	void process(HttpRequest request);
	
	/**
	 * Manipulate a HTTP response.
	 *
	 * @param response HttpResponse.
	 */
	void process(HttpResponse response);
}
