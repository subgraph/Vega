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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.http.requests.custom.VegaHttpEntityEnclosingUriRequest;
import com.subgraph.vega.http.requests.custom.VegaHttpUriRequest;

public class HttpRequestBuilder extends HttpMessageBuilder implements IHttpRequestBuilder {
	private String scheme = "http";
	private String host = "";
	private int hostPort = 80;
	private String method = "";
	private String path = "";

	@Override
	public synchronized void clear() {
		super.clear();
		scheme = "http";
		host = "";
		hostPort = 80;
		method = "";
		path = "";
	}

	@Override
	public synchronized void setFromRequest(IRequestLogRecord record) throws URISyntaxException {
		setFromRequest(record.getRequest());
		setFromHttpHost(record.getHttpHost());
	}

	@Override
	public synchronized void setFromRequest(HttpRequest request) throws URISyntaxException {
		setParams(request.getParams().copy());
		if(request instanceof HttpUriRequest) {
			setSchemeAndHostFromUri(((HttpUriRequest) request).getURI());
		}
		setFromRequestLine(request.getRequestLine());
		setHeaders(request.getAllHeaders());

		if (request instanceof HttpEntityEnclosingRequest) {
			setEntity(((HttpEntityEnclosingRequest) request).getEntity());
		} else {
			setEntity(null);
		}
	}

	@Override
	public synchronized void setFromRequestLine(RequestLine requestLine) throws URISyntaxException {
		method = requestLine.getMethod();
		path = requestLine.getUri();
		setProtocolVersion(requestLine.getProtocolVersion());
	}

	@Override
	public synchronized void setFromUri(URI uri) {
		setSchemeAndHostFromUri(uri);
		setPathFromUri(uri);
	}

	private void setSchemeAndHostFromUri(URI uri) {
		final HttpHost httpHost = URIUtils.extractHost(uri);
		if(httpHost == null) {
			return;
		}
		if(httpHost.getSchemeName() != null) {
			scheme = httpHost.getSchemeName();
			if(httpHost.getHostName() != null) {
				host = httpHost.getHostName();
				hostPort = httpHost.getPort();
				if(hostPort == -1) {
					hostPort = getSchemeDefaultPort(scheme);
				}
			}
		}
	}

	private void setPathFromUri(URI uri) {
		path = uri.getRawPath();
		if (path != null) {
			if (path.length() == 0 || path.charAt(0) != '/') {
				path = '/' + path;
			}
		} else {
			path = "";
		}
		if (uri.getRawQuery() != null) {
			path += '?' + uri.getRawQuery();
		}
		if (uri.getRawFragment() != null) {
			path += '#' + uri.getRawFragment();
		}		
	}
	
	@Override
	public synchronized void setFromHttpHost(HttpHost host) {
		scheme = host.getSchemeName();
		if (scheme == null) {
			scheme = "http";
		}

		this.host = host.getHostName();
		hostPort = host.getPort();
		if (hostPort == -1) {
			hostPort = getSchemeDefaultPort(scheme);
		}
	}
	
	private int getSchemeDefaultPort(final String scheme) {
		if (scheme.equals("https")) {
			return 443;
		} else {
			return 80;
		}
	}
	
	private boolean isSchemeDefaultPort(final String scheme, int port) {
		if (scheme.equals("https")) {
			if (port == 443) {
				return true;
			}
		} else {
			if (port == 80) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized void setScheme(String scheme) {
		if (scheme != null) {
			this.scheme = scheme.trim();
		} else {
			this.scheme = "http";
		}
	}

	@Override
	public synchronized String getScheme() {
		return scheme;
	}

	@Override
	public synchronized void setHost(String host) {
		if (host != null) {
			this.host = host.trim();
		} else {
			this.host = null;
		}
	}

	@Override
	public synchronized String getHost() {
		return host;
	}

	@Override
	public synchronized void setHostPort(int port) {
		if (port != -1) {
			this.hostPort = port;
		} else {
			this.hostPort = 80;
		}
	}

	@Override
	public synchronized int getHostPort() {
		return hostPort;
	}

	@Override
	public synchronized void setMethod(String method) {
		if (method != null) {
			this.method = method.trim();
		} else {
			this.method = null;
		}
	}

	@Override
	public synchronized String getMethod() {
		return method;
	}

	@Override
	public synchronized String getPath() {
		return path;
	}

	@Override
	public synchronized void setPath(String path) {
		if (path != null) {
			String tmp = path.trim();
			if (tmp.length() == 0 || tmp.charAt(0) != '/') {
				tmp = '/' + tmp;
			}
			this.path = tmp;
		} else {
			this.path = null;
		}
	}

	@Override
	public synchronized String getRequestLine() {
		final StringBuilder buf = new StringBuilder();
		if (method != null) {
			buf.append(method);
		}
		if (path != null) {
			if (buf.length() != 0) {
				buf.append(' ');
			}
			buf.append(path);
		}
		ProtocolVersion protocolVersion = getProtocolVersion();
		if (protocolVersion != null) {
			if (buf.length() != 0) {
				buf.append(' ');
			}
			buf.append(protocolVersion.toString());
		}
		return buf.toString();
	}

	@Override
	public synchronized HttpUriRequest buildRequest(boolean setHeadersEntity) throws URISyntaxException {
		final HttpUriRequest request = createRequest( buildHost() );
		
		HttpParams params = getParams();
		if (params == null) {
			params = new BasicHttpParams();
		}
		ProtocolVersion protocolVersion = getProtocolVersion();
		if (protocolVersion != null) {
			HttpProtocolParams.setVersion(request.getParams(), protocolVersion);
		}
		request.setParams(params);

		if (setHeadersEntity) {
			setHeadersEntity();
		}
		IHttpHeaderBuilder[] headers = getHeaders();
		for (IHttpHeaderBuilder h: headers) {
			request.addHeader(h.buildHeader());
		}

		return request;
	}
	
	private HttpHost buildHost() {
		if (host == null || host.length() == 0) {
			throw new IllegalArgumentException("Invalid host");
		}
		if(isSchemeDefaultPort(scheme, hostPort)) {
			return new HttpHost(host, -1, scheme);
		} else {
			return new HttpHost(host, hostPort, scheme);
		}
	}

	private HttpUriRequest createRequest(HttpHost host) {
		final String requestPath = (path == null) ? "" : path;
		final HttpEntity entity = getEntity();
		if(entity != null) {
			return createEntityEnclosingRequest(host, requestPath, entity);
		} else {
			return new VegaHttpUriRequest(host, method, requestPath);
		}
	}
	
	private HttpUriRequest createEntityEnclosingRequest(HttpHost host, String requestPath, HttpEntity entity) {
		final VegaHttpEntityEnclosingUriRequest request = new VegaHttpEntityEnclosingUriRequest(host, method, requestPath);
		request.setEntity(entity);
		return request;
	}
}
