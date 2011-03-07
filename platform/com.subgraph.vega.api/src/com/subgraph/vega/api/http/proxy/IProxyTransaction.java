package com.subgraph.vega.api.http.proxy;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public interface IProxyTransaction {
	void setEventHandler(IProxyTransactionEventHandler eventHandler);
	HttpHost getHttpHost();
	URI getUri();
	boolean hasRequest();
	HttpRequest getRequest();
	boolean hasResponse();
	IHttpResponse getResponse();
	void doForward();
	void doDrop();
}
