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
package com.subgraph.vega.ui.httpeditor;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;

public class HttpHeaderTextRenderer {
	
	public String getRequestAsText(HttpRequest request) {
		final StringBuilder sb = new StringBuilder();
		renderRequestLine(sb, request);
		renderAllHeaders(sb, request);
		return sb.toString();
	}

	public String getRequestAsText(IHttpRequestBuilder request) {
		final StringBuilder sb = new StringBuilder();
		renderRequestLine(sb, request);
		renderAllHeaders(sb, request);
		return sb.toString();
	}
	
	public String getResponseAsText(HttpResponse response) {
		final StringBuilder sb = new StringBuilder();
		renderStatusLine(sb, response);
		renderAllHeaders(sb, response);		
		return sb.toString();
	}
	
	public String getResponseAsText(IHttpResponseBuilder builder) {
		final StringBuilder sb = new StringBuilder();
		renderStatusLine(sb, builder);
		renderAllHeaders(sb, builder);		
		return sb.toString();
	}

	private void renderRequestLine(StringBuilder sb, HttpRequest request) {
		sb.append(request.getRequestLine().toString());
		sb.append('\n');
	}

	private void renderRequestLine(StringBuilder sb, IHttpRequestBuilder builder) {
		final String requestLine = builder.getRequestLine();
		if (requestLine != null) {
			sb.append(requestLine);
			sb.append('\n');
		}
	}
	
	private void renderStatusLine(StringBuilder sb, HttpResponse response) {
		sb.append(response.getStatusLine().toString());
		sb.append('\n');
	}
	
	private void renderStatusLine(StringBuilder sb, IHttpResponseBuilder response) {
		sb.append(response.getStatusLine().toString());
		sb.append('\n');
	}

	private void renderAllHeaders(StringBuilder sb, HttpMessage message) {
		for(Header h: message.getAllHeaders()) { 
			renderHeader(sb, h);
		}
		sb.append('\n');
	}

	private void renderAllHeaders(StringBuilder sb, IHttpMessageBuilder builder) {
		for(IHttpHeaderBuilder h: builder.getHeaders()) { 
			renderHeader(sb, h);
		}
		sb.append('\n');
	}

	private void renderHeader(StringBuilder sb, Header header) {
		sb.append(header.getName());
		sb.append(": ");
		sb.append(header.getValue());
		sb.append("\n");
	}

	private void renderHeader(StringBuilder sb, IHttpHeaderBuilder builder) {
		// REVISIT: put raw header, if applicable
		sb.append(builder.getName());
		sb.append(": ");
		sb.append(builder.getValue());
		sb.append("\n");
	}

}
