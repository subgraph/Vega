package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

public interface IHttpResponseProcessor {
	void processResponse(HttpRequest request, IHttpResponse response, HttpContext context);
}
