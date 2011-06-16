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
package com.subgraph.vega.impl.scanner;

import java.net.URI;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicRequestLine;

public class VegaHttpRequest extends HttpRequestBase {
	static public VegaHttpRequest createGET(URI uri) {
		return new VegaHttpRequest("GET", uri);
	}
	
	static public VegaHttpRequest createPUT(URI uri) {
		return new VegaHttpRequest("PUT", uri);
	}
	
	private final String method;
	
	
	public VegaHttpRequest(String method, URI uri) {
		setURI(uri);
		this.method = method;
	}
	
	
	@Override
	public RequestLine getRequestLine() {
		final String method = getMethod();
		final ProtocolVersion ver = getProtocolVersion();
		return new BasicRequestLine(method, getUriText(), ver);
	}
	
	private String getUriText() {
		
		final URI u = getURI();
		if(u == null) 
			return "/";
		else
			return u.toASCIIString();
	}
	
	
	@Override
	public String getMethod() {
		return method;
	}
}
