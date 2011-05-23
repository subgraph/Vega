package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpUriRequest;

public interface IHttpMutableRequest extends HttpUriRequest, HttpMessage {
	void setMethod(String method);
}
