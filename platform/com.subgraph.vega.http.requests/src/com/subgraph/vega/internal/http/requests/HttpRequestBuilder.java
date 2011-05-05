package com.subgraph.vega.internal.http.requests;

import java.net.URI;
import java.net.URISyntaxException;

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

public class HttpRequestBuilder extends HttpMessageBuilder implements IHttpRequestBuilder {
	private String scheme = "http";
	private String host = "";
	private int hostPort = 80;
	private String method = "";
	private String path = "";
	private String rawRequestLine;

	@Override
	public void clear() {
		super.clear();
		scheme = "http";
		host = "";
		hostPort = 80;
		method = "";
		path = "";
		rawRequestLine = null;
	}

	@Override
	public void setFromRequest(IRequestLogRecord record) throws URISyntaxException {
		setFromRequest(record.getRequest());
		setFromHttpHost(record.getHttpHost());
	}

	@Override
	public void setFromRequest(HttpRequest request) throws URISyntaxException {
		setParams(request.getParams().copy());
		setFromRequestLine(request.getRequestLine());
		setHeaders(request.getAllHeaders());

		if (request instanceof HttpEntityEnclosingRequest) {
			setEntity(((HttpEntityEnclosingRequest) request).getEntity());
		} else {
			setEntity(null);
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

		setProtocolVersion(requestLine.getProtocolVersion());

		if (requestLine instanceof RawRequestLine) {
			rawRequestLine = ((RawRequestLine)requestLine).toString();
		} else {
			rawRequestLine = null;
		}
	}

	@Override
	public void setFromUri(URI uri) {
		scheme = uri.getScheme();
		if (scheme == null) {
			scheme = "http";
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
	public void setRawRequestLine(String line) {
		this.rawRequestLine = line;
	}

	@Override
	public String getRawRequestLine() {
		return rawRequestLine;
	}
	
	@Override
	public String getRequestLine() {
		if (rawRequestLine != null) {
			return rawRequestLine;
		} else {
			String requestLine = method + " " + path;
			ProtocolVersion protocolVersion = getProtocolVersion();
			if (protocolVersion != null) {
				requestLine += " " + protocolVersion.toString();
			}
			return requestLine;
		}
	}

	@Override
	public HttpUriRequest buildRequest() throws URISyntaxException {
		if (host == null || host.length() == 0) {
			throw new IllegalArgumentException("Invalid host");
		}
		
		final URI requestUri = new URI("http://" + host + ":" + Integer.toString(hostPort) + path);
		IHttpRawRequest request;
		HttpEntity entity = getEntity();
		
		if (entity != null) {
			HttpEntityEnclosingRawRequest entityRequest = new HttpEntityEnclosingRawRequest(rawRequestLine, method, requestUri);
			entityRequest.setEntity(entity);
			request = entityRequest;
		} else {
			request = new HttpRawRequest(rawRequestLine, method, requestUri);
		}

		HttpParams params = getParams();
		if (params == null) {
			params = new BasicHttpParams();
		}
		ProtocolVersion protocolVersion = getProtocolVersion();
		if (protocolVersion != null) {
			HttpProtocolParams.setVersion(request.getParams(), protocolVersion);
		}
		request.setParams(params);

		IHttpHeaderBuilder[] headers = getHeaders();
		for (IHttpHeaderBuilder h: headers) {
			request.addHeader(h.buildHeader());
		}
		return request;
	}

}
