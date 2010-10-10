package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpResponse;

public interface IHttpResponse {
	HttpResponse getRawResponse();
	String getBodyAsString();

}
