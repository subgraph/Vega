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
package com.subgraph.vega.internal.model.requests;

import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;


public class RequestLogRecord implements IRequestLogRecord, Activatable {
	final long requestId;
	private final HttpRequest request;
	private final HttpHost host;
	
	private String hostname;
	private String requestMethod;
	private String requestPath;
	private String requestHeaders;
	
	private int responseCode;
	private int responseLength;
	private String responseHeaders;

	private HttpResponse response;
	private final long timestamp;
	private long requestTimeMs;

	private transient Activator activator;

	RequestLogRecord() {
		requestId = 0;
		request = null;
		response = null;
		host = null;
		timestamp = 0;
		requestTimeMs = -1;
		setCachedRequestFields(null, null);
		setCachedResponseFields(null);
	}

	RequestLogRecord(long requestId, HttpRequest request, HttpResponse response, HttpHost host, long requestTimeMs) {
		this.requestId = requestId;
		this.request = request;
		this.response = response;
		this.host = host;
		this.timestamp = new Date().getTime();
		this.requestTimeMs = requestTimeMs;
		setCachedRequestFields(request, host);
		setCachedResponseFields(response);
	}

	RequestLogRecord(long requestId, HttpRequest request, HttpHost host, long requestTimeMs) {
		this(requestId, request, null, host, requestTimeMs);
	}

	private void setCachedRequestFields(HttpRequest request, HttpHost host) {
		if(request == null) {
			hostname = null;
			requestMethod = null;
			requestHeaders = null;
			requestPath = null;
		} else {
			hostname = host.getHostName();
			requestMethod = request.getRequestLine().getMethod();
			requestHeaders = headersToString(request.getAllHeaders());
			requestPath = request.getRequestLine().getUri();
		}
	}
	
	private void setCachedResponseFields(HttpResponse response) {
		if(response == null) {
			responseCode = 0;
			responseLength = 0;
			responseHeaders = null;
		} else {
			responseCode = response.getStatusLine().getStatusCode();
			responseLength = (int) getLengthFromResponse(response);
			responseHeaders = headersToString(response.getAllHeaders());
		}
	}
	
	private String headersToString(Header[] headers) {
		final StringBuilder sb = new StringBuilder();
		for(Header h: headers) {
			sb.append(h.getName());
			sb.append(": ");
			sb.append(h.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private long getLengthFromResponse(HttpResponse response) {
		final Header lengthHeader = response.getFirstHeader("Content-Length");
		if(lengthHeader != null) {
			try {
				return Long.parseLong(lengthHeader.getValue());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		if(response.getEntity() == null)
			return 0;
		return response.getEntity().getContentLength();
	}
	
	void setResponse(HttpResponse response) {
		activate(ActivationPurpose.WRITE);
		this.response = response;
		setCachedResponseFields(response);
	}
	
	String getHostname() { return hostname; }
	String getRequestMethod() { return requestMethod; }
	String getRequestHeaders() { return requestHeaders; }
	String getRequestPath() { return requestPath; }
	int getResponseCode() { return responseCode; }
	int getResponseLength() { return responseLength; }
	String getResponseHeaders() { return responseHeaders; }
	
	@Override
	public long getRequestId() {
		activate(ActivationPurpose.READ);
		return requestId;
	}

	@Override
	public long getTimestamp() {
		activate(ActivationPurpose.READ);
		return timestamp;
	}

	@Override
	public long getRequestMilliseconds() {
		activate(ActivationPurpose.READ);
		return requestTimeMs;
	}

	@Override
	public HttpRequest getRequest() {
		activate(ActivationPurpose.READ);
		return request;
	}

	@Override
	public HttpResponse getResponse() {
		activate(ActivationPurpose.READ);
		synchronized(response) {
			return response;
		}
	}

	@Override
	public HttpHost getHttpHost() {
		activate(ActivationPurpose.READ);
		return host;
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}

		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}

		this.activator = activator;
	}

}
