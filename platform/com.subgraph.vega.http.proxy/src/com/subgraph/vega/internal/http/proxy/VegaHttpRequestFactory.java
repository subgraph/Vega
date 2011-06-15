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
package com.subgraph.vega.internal.http.proxy;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHttpRequest;

/**
 * Specialized HttpRequestFactory which allows requests with the 'CONNECT' method for supporting 
 * SSL interception.
 */
public class VegaHttpRequestFactory extends DefaultHttpRequestFactory {
	
	private boolean isConnectMethod(String method) {
		return (method != null && method.equalsIgnoreCase("CONNECT"));
	}
	
	@Override
	public HttpRequest newHttpRequest(final RequestLine requestLine) throws MethodNotSupportedException {
		if(isConnectMethod(requestLine.getMethod())) {
			return new BasicHttpRequest(requestLine);
		} else {
			return super.newHttpRequest(requestLine);
		}
	}
	
	@Override
	public HttpRequest newHttpRequest(final String method, final String uri) throws MethodNotSupportedException {
		if(isConnectMethod(method)) {
			return new BasicHttpRequest(method, uri);
		} else {
			return super.newHttpRequest(method, uri);
		}
	}
}
