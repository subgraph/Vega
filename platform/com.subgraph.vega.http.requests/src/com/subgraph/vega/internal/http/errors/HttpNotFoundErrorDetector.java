package com.subgraph.vega.internal.http.errors;

import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public class HttpNotFoundErrorDetector {
	public boolean isNotFoundErrorResponse(HttpRequest request, IHttpResponse response) {
		return response.getRawResponse().getStatusLine().getStatusCode() == 404;
	}
}
