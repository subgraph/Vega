package com.subgraph.vega.internal.http.requests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRawRequest;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.http.requests.custom.HttpEntityEnclosingRawRequest;
import com.subgraph.vega.http.requests.custom.HttpRawRequest;
import com.subgraph.vega.http.requests.custom.RawRequestLine;

public class HttpRequestBuilder implements IHttpRequestBuilder {
	private HttpParams params;
	
	private String scheme = "http";
	private String host = "";
	private int hostPort = 80;

	private String rawRequestLine;
	private String method = "";
	private String path = "";
	private ProtocolVersion protocolVersion = null;

	private final ArrayList<HttpHeaderBuilder> headerList = new ArrayList<HttpHeaderBuilder>();

	private HttpEntity entity;

	public HttpRequestBuilder() {
	}
	
	@Override
	public void clear() {
		params = null;
		scheme = "http";
		host = "";
		hostPort = 80;
		rawRequestLine = null;
		method = "";
		path = "";
		protocolVersion = null;
		headerList.clear();
		entity = null;
	}

	@Override
	public void setParams(HttpParams params) {
		this.params = params;
	}

	@Override
	public HttpParams getParams() {
		return params;
	}

	@Override
	public void setFromRequest(IRequestLogRecord request) throws URISyntaxException {
		setFromRequest(request.getRequest());
		setFromHttpHost(request.getHttpHost());
	}

	@Override
	public void setFromRequest(HttpRequest request) throws URISyntaxException {
		params = request.getParams().copy();

		setFromRequestLine(request.getRequestLine());
		headerList.clear();
		for (Header h: request.getAllHeaders()) {
			headerList.add(new HttpHeaderBuilder(h));
		}

		if (request instanceof HttpEntityEnclosingRequest) {
			entity = ((HttpEntityEnclosingRequest) request).getEntity();
		} else {
			entity = null;
		}
	}

	@Override
	public void setFromRequestLine(RequestLine requestLine) throws URISyntaxException {
		method = requestLine.getMethod();

		final URI requestUri = new URI(requestLine.getUri());

		scheme = requestUri.getScheme();
		if (scheme == null) {
			scheme = "http";
		}

		host = requestUri.getHost();
		hostPort = requestUri.getPort();
		if (hostPort == -1) {
			if (scheme == "https") {
				hostPort = 443;
			} else {
				hostPort = 80;
			}
		}

		path = requestUri.getPath();
		if (requestUri.getQuery() != null) {
			path += "?" + requestUri.getQuery();
		}
		if (requestUri.getFragment() != null) {
			path += "#" + requestUri.getFragment();
		}

		protocolVersion = requestLine.getProtocolVersion();

		if (requestLine instanceof RawRequestLine) {
			rawRequestLine = ((RawRequestLine)requestLine).toString();
		}
	}

	@Override
	public void setFromUri(URI uri) {

		if (uri.getScheme() != null) {
			scheme = uri.getScheme();
		}

		if (uri.getHost() != null) {
			host = uri.getHost();
			if (uri.getPort() != -1) {
				hostPort = uri.getPort();
			} else {
				if (scheme == "https") {
					hostPort = 443;
				} else {
					hostPort = 80;
				}
			}
		}

		
		path = uri.getPath();
		if (uri.getQuery() != null) {
			path += "?" + uri.getQuery();
		}
		if (uri.getFragment() != null) {
			path += "#" + uri.getFragment();
		}
	}

	@Override
	public void setFromHttpHost(HttpHost host) {
		scheme = host.getSchemeName();
		if (scheme == null) {
			scheme = "http";
		}

		this.host = host.getHostName();
		hostPort = host.getPort();
		if (hostPort == -1) {
			if (scheme == "https") {
				hostPort = 443;
			} else {
				hostPort = 80;
			}
		}
	}
	
	@Override
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHostPort(int port) {
		this.hostPort = port;
	}

	@Override
	public int getHostPort() {
		return hostPort;
	}

	@Override
	public void setRawRequestLine(String line) {
		this.rawRequestLine = line;
	}

	@Override
	public String getRawRequestLine() {
		return rawRequestLine;
	}
	
	@Override
	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion; 
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public String getRequestLine() {
		if (rawRequestLine != null) {
			return rawRequestLine;
		} else {
			String requestLine = method + " " + path;
			if (protocolVersion != null) {
				requestLine += " " + protocolVersion.toString();
			}
			return requestLine;
		}
	}

	@Override
	public HttpHeaderBuilder addHeader(String name, String value) {
		HttpHeaderBuilder header = new HttpHeaderBuilder(name, value);
		headerList.add(header);
		return header;
	}

	@Override
	public void removeHeader(IHttpHeaderBuilder header) {
		headerList.remove(header);
	}

	@Override
	public void clearHeaders() {
		headerList.clear();
	}
	
	@Override
	public void swapHeader(int idx1, int idx2) {
		if (idx1 < headerList.size() && idx2 < headerList.size() && idx1 != idx2) {
			HttpHeaderBuilder tmp = headerList.set(idx1, headerList.get(idx2));
			headerList.set(idx2, tmp);
		}
	}
	
	@Override
	public int getHeaderIdxOf(IHttpHeaderBuilder header) {
		return headerList.indexOf(header);
	}

	@Override
	public int getHeaderCnt() {
		return headerList.size();
	}

	@Override
	public IHttpHeaderBuilder getHeader(int idx) {
		return headerList.get(idx);
	}

	@Override
	public IHttpHeaderBuilder[] getHeaders() {
		return headerList.toArray(new HttpHeaderBuilder[headerList.size()]);
	}

	@Override
	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	@Override
	public HttpEntity getEntity() {
		return entity;
	}

	@Override
	public HttpUriRequest buildRequest() throws URISyntaxException {
		if (host == null || host.length() == 0) {
			throw new IllegalArgumentException("Invalid host");
		}
		
		final URI requestUri = new URI("http://" + host + ":" + Integer.toString(hostPort) + path);
		IHttpRawRequest request;

		if (entity != null) {
			HttpEntityEnclosingRawRequest entityRequest = new HttpEntityEnclosingRawRequest(rawRequestLine, method, requestUri);
			entityRequest.setEntity(entity);
			request = entityRequest;
		} else {
			request = new HttpRawRequest(rawRequestLine, method, requestUri);
		}

		if (params == null) {
			params = new BasicHttpParams();
		}
		request.setParams(params);
		if (protocolVersion != null) {
			HttpProtocolParams.setVersion(request.getParams(), protocolVersion);
		}
		
		for (HttpHeaderBuilder h: headerList) {
			request.addHeader(h.buildHeader());
		}
		return request;
	}

}
