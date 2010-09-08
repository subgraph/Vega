package com.subgraph.vega.http.proxy;

import java.net.InetAddress;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IProxyTransaction {
	InetAddress getTargetAddress();
	HttpHost getHttpHost();
	HttpRequest getRequest();
	HttpResponse getResponse();
}
