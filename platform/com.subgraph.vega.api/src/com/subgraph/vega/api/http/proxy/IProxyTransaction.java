package com.subgraph.vega.api.http.proxy;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public interface IProxyTransaction {
	HttpHost getHttpHost();
	HttpRequest getRequest();
	IHttpResponse getResponse();
}
