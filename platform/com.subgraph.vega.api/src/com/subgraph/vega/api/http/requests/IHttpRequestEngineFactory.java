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

import org.apache.http.HttpHost;

import com.subgraph.vega.api.model.requests.IRequestOrigin;

public interface IHttpRequestEngineFactory {
	/**
	 * Default User-Agent string.
	 */
	static final String DEFAULT_USER_AGENT = 
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; InfoPath.1; .NET CLR " +
	          "2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; Vega/1.0";
	
	/**
	 * Set a simple one-hop proxy to be set for all HttpClients instantiated from this request engine factory.
	 * @param proxy Simple one hop proxy, or null to unset.
	 */
	void setProxy(HttpHost proxy);

	IHttpRequestEngineConfig createConfig();
	IHttpRequestEngine createRequestEngine(IHttpRequestEngine.EngineConfigType type, IHttpRequestEngineConfig config, IRequestOrigin requestOrigin);

	/**
	 * Instantiate a HttpRequestBuilder.
	 * 
	 * @return HttpRequestBuilder instance.
	 */
	IHttpRequestBuilder createRequestBuilder();

	/**
	 * Instantiate a HttpResponseBuilder.
	 *
	 * @return HttpResponseBuilder instance.
	 */
	IHttpResponseBuilder createResponseBuilder();
}
