package com.subgraph.vega.api.http.proxy;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IProxyTransaction {
	HttpHost getHttpHost();
	HttpRequest getRequest();
	HttpResponse getResponse();
}
