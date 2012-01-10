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
package com.subgraph.vega.http.requests.builder;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class HttpResponseBuilder extends HttpMessageBuilder implements IHttpResponseBuilder {
	private int statusCode = -200;
	private String reasonPhrase = "";
	
	@Override
	public synchronized void clear() {
		super.clear();
		statusCode = 200;
		reasonPhrase = "";
	}

	@Override
	public synchronized void setFromResponse(IRequestLogRecord record) {
		setFromResponse(record.getResponse());
	}

	@Override
	public synchronized void setFromResponse(HttpResponse response) {
		setFromStatusLine(response.getStatusLine());
		setHeaders(response.getAllHeaders());
		setEntity(response.getEntity());
	}

	@Override
	public synchronized void setFromStatusLine(StatusLine statusLine) {
		setProtocolVersion(statusLine.getProtocolVersion());
		statusCode = statusLine.getStatusCode();
		reasonPhrase = statusLine.getReasonPhrase();
	}

	@Override
	public synchronized String getStatusLine() {
		if (getProtocolVersion() != null) {
			return getProtocolVersion().toString() + " " + Integer.toString(statusCode) + " " + reasonPhrase;
		} else {
			return "";
		}
	}

	@Override
	public synchronized HttpResponse buildResponse() {
		BasicHttpResponse response = new BasicHttpResponse(getProtocolVersion(), statusCode, reasonPhrase);
		
		setHeadersEntity();
		IHttpHeaderBuilder[] headers = getHeaders();
		for (IHttpHeaderBuilder h: headers) {
			response.addHeader(h.buildHeader());
		}

		response.setEntity(getEntity());
		
		return response;
	}

}
