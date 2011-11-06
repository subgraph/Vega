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

import org.apache.http.HttpRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestModifier;

public class HttpRequestModifierCookies implements IHttpRequestModifier {
	private final IHttpRequestEngineConfig config;
	
	public HttpRequestModifierCookies(IHttpRequestEngineConfig config) {
		this.config = config;
	}
	
	@Override
	public void process(HttpRequest request, HttpContext context) {
		context.setAttribute(ClientContext.COOKIE_STORE, config.getCookieStore());
	}
}
