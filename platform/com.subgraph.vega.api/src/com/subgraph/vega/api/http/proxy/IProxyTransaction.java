package com.subgraph.vega.api.http.proxy;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public interface IProxyTransaction {
	enum TransactionDirection {
		DIRECTION_REQUEST("request"),
		DIRECTION_RESPONSE("response");
		private final String name;
		TransactionDirection(String name) { this.name = name; }
		String getName() { return name; }
	};

	void setEventHandler(IProxyTransactionEventHandler eventHandler);
	HttpHost getHttpHost();
	URI getUri();
	boolean hasRequest();
	HttpRequest getRequest();
	boolean hasResponse();
	IHttpResponse getResponse();
	void doForward();
	void doDrop();
	void signalComplete();
}
