package com.subgraph.vega.internal.http.proxy;

import java.net.InetAddress;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.http.proxy.IProxyTransaction;

public class ProxyTransaction implements IProxyTransaction {

	private final HttpRequest request;
	private final HttpResponse response;
	private final HttpHost httpHost;
	private final InetAddress address;


	ProxyTransaction(HttpRequest request, HttpResponse response, HttpHost httpHost, InetAddress address) {
		this.request = request;
		this.response = response;
		this.httpHost = httpHost;
		this.address = address;
	}
	
	
	@Override
	public InetAddress getTargetAddress() {
		return address;
	}

	@Override
	public HttpRequest getRequest() {
		return request;
	}

	@Override
	public HttpResponse getResponse() {
		return response;
	}


	@Override
	public HttpHost getHttpHost() {
		return httpHost;
	}

}
