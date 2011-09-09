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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableArrayList;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.tags.ITag;

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
	private ActivatableArrayList<ITag> tagList;
	private transient Activator activator;

	RequestLogRecord(long requestId, HttpRequest request, HttpResponse response, HttpHost host, long requestTimeMs, List<ITag> tagList) {
		this.requestId = requestId;
		this.request = request;
		this.response = response;
		this.host = host;
		this.timestamp = new Date().getTime();
		this.requestTimeMs = requestTimeMs;
		this.tagList = new ActivatableArrayList<ITag>(tagList);
		setCachedRequestFields(request, host);
		setCachedResponseFields(response);
	}

	RequestLogRecord(long requestId, HttpRequest request, HttpHost host, long requestTimeMs, List<ITag> tagList) {
		this(requestId, request, null, host, requestTimeMs, tagList);
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
		activate(ActivationPurpose.READ);
		this.response = response;
		setCachedResponseFields(response);
		activate(ActivationPurpose.WRITE);
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

	@Override
	public Collection<ITag> getAllTags() {
		activate(ActivationPurpose.READ);
		return Collections.unmodifiableList(new ArrayList<ITag>(tagList));
	}

	@Override
	public int getTagCount() {
		activate(ActivationPurpose.READ);
		return tagList.size();
	}

	@Override
	public void setTags(Collection<ITag> tags) {
		activate(ActivationPurpose.READ);
		tagList.clear();
		tagList.addAll(tags);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void addTag(ITag tag) {
		activate(ActivationPurpose.READ);
		tagList.add(tag);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void removeTag(ITag tag) {
		activate(ActivationPurpose.READ);
		tagList.remove(tag);
		activate(ActivationPurpose.WRITE);
	}

}
