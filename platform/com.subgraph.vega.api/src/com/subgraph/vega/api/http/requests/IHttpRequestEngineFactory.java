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
import org.apache.http.client.HttpClient;

public interface IHttpRequestEngineFactory {
	/**
	 * Set a simple one-hop proxy to be set for all HttpClients instantiated from this request engine factory.
	 * @param proxy Simple one hop proxy, or null to unset.
	 */
	void setProxy(HttpHost proxy);

	IHttpRequestEngineConfig createConfig();
	HttpClient createBasicClient();
	HttpClient createUnencodingClient();
	IHttpRequestEngine createRequestEngine(HttpClient client, IHttpRequestEngineConfig config);
}
