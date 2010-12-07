package com.subgraph.vega.api.model.requests;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IRequestLogRecord {
	long getRequestId();
	long getTimestamp();
	HttpHost getHttpHost();
	HttpRequest getRequest();
	HttpResponse getResponse();
}
