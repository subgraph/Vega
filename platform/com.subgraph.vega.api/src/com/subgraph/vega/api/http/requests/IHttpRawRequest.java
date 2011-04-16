package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpUriRequest;

public interface IHttpRawRequest extends HttpUriRequest, HttpMessage {
	void setRawRequestLine(String line);
	String getRawRequestLine();
	void setMethod(String method);
}
