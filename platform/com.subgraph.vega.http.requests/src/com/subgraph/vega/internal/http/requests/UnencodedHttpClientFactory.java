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
package com.subgraph.vega.internal.http.requests;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.internal.http.requests.connection.UnencodingThreadSafeClientConnectionManager;

public class UnencodedHttpClientFactory extends AbstractHttpClientFactory {
	static HttpClient createHttpClient() {
		final HttpParams params = createHttpParams();
		final ClientConnectionManager ccm = createConnectionManager(params);
		final DefaultHttpClient client = new DefaultHttpClient(ccm, params);
		
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		client.addRequestInterceptor(new RequestCopyHeadersInterceptor());
		return client;
	}

	private static ClientConnectionManager createConnectionManager(HttpParams params) {
		final SchemeRegistry sr = createSchemeRegistry();
		return new UnencodingThreadSafeClientConnectionManager(sr);
	}

	private static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(params, IHttpRequestEngineFactory.DEFAULT_USER_AGENT);
		return params;
	}

}
