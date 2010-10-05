package com.subgraph.vega.api.http.requests;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public interface IHttpRequestEngine {
	HttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException;
	HttpResponse sendRequest(HttpUriRequest request) throws IOException, ClientProtocolException;
	void registerResponseProcessor(IHttpResponseProcessor processor);
}
