package com.subgraph.vega.internal.http.proxy;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class ProxyTransaction implements IProxyTransaction {
	private final HttpRequest request;
	private final IHttpResponse response;
	private final HttpHost httpHost;

	ProxyTransaction(HttpRequest request, IHttpResponse response, HttpHost httpHost) {
		this.request = request;
		this.response = response;
		this.httpHost = httpHost;
	}

	@Override
	public HttpHost getHttpHost() {
		return httpHost;
	}

	@Override
	public HttpRequest getRequest() {
		return request;
	}

	@Override
	public IHttpResponse getResponse() {
		return response;
	}
}
