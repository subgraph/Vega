/*******************************************************************************
 * Copyright (c) 2012 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.model.requests;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpParams;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.http.requests.builder.HttpHeaderBuilder;
import com.subgraph.vega.http.requests.builder.HttpRequestBuilder;

/**
 * Decorator for IHttpRequestBuilder to maintain data in the database using transparent activation.
 */
public class HttpRequestBuilderStorable extends HttpRequestBuilder implements IHttpRequestBuilder, Activatable {
	protected transient Activator activator;

	@Override
	public void clear() {
		activate(ActivationPurpose.READ);
		super.clear();
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void setParams(HttpParams params) {
		activate(ActivationPurpose.READ);
		super.setParams(params);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public HttpParams getParams() {
		activate(ActivationPurpose.READ);
		return super.getParams();
	}

	@Override
	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		activate(ActivationPurpose.READ);
		super.setProtocolVersion(protocolVersion);
		activate(ActivationPurpose.WRITE);		
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		activate(ActivationPurpose.READ);
		return super.getProtocolVersion();
	}

	@Override
	public void setHeaders(Header[] headers) {
		activate(ActivationPurpose.READ);
		super.setHeaders(headers);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public HttpHeaderBuilder addHeader(String name, String value) {
		activate(ActivationPurpose.READ);
		final HttpHeaderBuilder builder = super.addHeader(name, value);
		activate(ActivationPurpose.WRITE);
		return builder;
	}

	@Override
	public HttpHeaderBuilder setHeader(String name, String value) {
		activate(ActivationPurpose.READ);
		final HttpHeaderBuilder builder = super.setHeader(name, value);
		activate(ActivationPurpose.WRITE);
		return builder;
	}

	@Override
	public void removeHeader(IHttpHeaderBuilder header) {
		activate(ActivationPurpose.READ);
		super.removeHeader(header);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void removeHeaders(String name) {
		activate(ActivationPurpose.READ);
		super.removeHeaders(name);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void clearHeaders() {
		activate(ActivationPurpose.READ);
		super.clearHeaders();
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void swapHeader(int idx1, int idx2) {
		activate(ActivationPurpose.READ);
		super.swapHeader(idx1, idx2);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public int getHeaderIdxOf(IHttpHeaderBuilder next) {
		activate(ActivationPurpose.READ);
		return super.getHeaderIdxOf(next);
	}

	@Override
	public int getHeaderCnt() {
		activate(ActivationPurpose.READ);
		return super.getHeaderCnt();
	}

	@Override
	public IHttpHeaderBuilder getHeader(int idx) {
		activate(ActivationPurpose.READ);
		return super.getHeader(idx);
	}

	@Override
	public IHttpHeaderBuilder[] getHeaders() {
		activate(ActivationPurpose.READ);
		return super.getHeaders();
	}

	@Override
	public void setEntity(HttpEntity entity) {
		activate(ActivationPurpose.READ);
		super.setEntity(entity);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public HttpEntity getEntity() {
		activate(ActivationPurpose.READ);
		return null;
	}

	@Override
	public void setFromRequest(IRequestLogRecord record) throws URISyntaxException {
		activate(ActivationPurpose.READ);
		super.setFromRequest(record);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void setFromRequest(HttpRequest request) throws URISyntaxException {
		activate(ActivationPurpose.READ);
		super.setFromRequest(request);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void setFromRequestLine(RequestLine requestLine) throws URISyntaxException {
		activate(ActivationPurpose.READ);
		super.setFromRequestLine(requestLine);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void setFromUri(URI uri) {
		activate(ActivationPurpose.READ);
		super.setFromUri(uri);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void setFromHttpHost(HttpHost host) {
		activate(ActivationPurpose.READ);
		super.setFromHttpHost(host);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void setScheme(String scheme) {
		activate(ActivationPurpose.READ);
		super.setScheme(scheme);		
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getScheme() {
		activate(ActivationPurpose.READ);
		return super.getScheme();
	}

	@Override
	public void setHost(String host) {
		activate(ActivationPurpose.READ);
		
	}

	@Override
	public String getHost() {
		activate(ActivationPurpose.READ);
		return super.getHost();
	}

	@Override
	public void setHostPort(int port) {
		activate(ActivationPurpose.READ);
		super.setHostPort(port);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public int getHostPort() {
		activate(ActivationPurpose.READ);
		return super.getHostPort();
	}

	@Override
	public void setMethod(String method) {
		activate(ActivationPurpose.READ);
		super.setMethod(method);
	}

	@Override
	public String getMethod() {
		activate(ActivationPurpose.READ);
		return super.getMethod();
	}

	@Override
	public void setPath(String path) {
		activate(ActivationPurpose.READ);
		super.setPath(path);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getPath() {
		activate(ActivationPurpose.READ);
		return super.getPath();
	}

	@Override
	public String getRequestLine() {
		activate(ActivationPurpose.READ);
		return super.getRequestLine();
	}

	@Override
	public HttpUriRequest buildRequest(boolean setHeadersEntity) throws URISyntaxException {
		activate(ActivationPurpose.READ);
		return super.buildRequest(setHeadersEntity);
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if (activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if (this.activator == activator) {
			return;
		}
		if (activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		this.activator = activator;			
	}

}
