package com.subgraph.vega.api.requestlog;

import java.net.InetAddress;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IRequestLogRecord {
	long getRequestId();
	long getTimestamp();
	HttpHost getHttpHost();
	InetAddress getAddress();
	HttpRequest getRequest();
	HttpResponse getResponse();
}
