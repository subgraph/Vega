package com.subgraph.vega.internal.http.proxy;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.proxy.IProxyTransaction;

public class ProxyTransaction implements IProxyTransaction {
	private final HttpRequest request;
	private final HttpResponse response;
	private final HttpHost httpHost;

	ProxyTransaction(HttpRequest request, HttpResponse response, HttpHost httpHost) {
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
	public HttpResponse getResponse() {
		return response;
	}
}
