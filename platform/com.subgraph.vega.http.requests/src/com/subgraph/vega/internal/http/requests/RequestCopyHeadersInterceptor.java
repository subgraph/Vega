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

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;

/**
 * Create a copy of the request which contains the headers which are actually sent.
 */
public class RequestCopyHeadersInterceptor implements HttpRequestInterceptor {

	@Override
	public void process(HttpRequest request, HttpContext context)
			throws HttpException, IOException {
		final HttpRequest copy = copyRequest(request);
		context.setAttribute(HttpRequestEngine.VEGA_SENT_REQUEST, copy);
	}
	
	private HttpRequest copyRequest(HttpRequest request) {
		if(request instanceof HttpEntityEnclosingRequest)
			return copyEntityEnclosingRequest((HttpEntityEnclosingRequest) request);
		else
			return copyBasicRequest(request);
	}

	private HttpRequest copyEntityEnclosingRequest(HttpEntityEnclosingRequest request) {
		final BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest(request.getRequestLine());
		r.setEntity(request.getEntity());
		copyHeaders(request, r);
		return r;
	}

	private HttpRequest copyBasicRequest(HttpRequest request) {
		if(request == null)
			return null;
		final HttpRequest r = new BasicHttpRequest(request.getRequestLine());
		copyHeaders(request, r);
		return r;
	}

	private static void copyHeaders(HttpMessage from, HttpMessage to) {
		for(Header h: from.getAllHeaders())
			to.addHeader(new BasicHeader(h.getName(), h.getValue()));
	}

}
