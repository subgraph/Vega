package com.subgraph.vega.api.http.requests;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public interface IHttpRequestEngine {
	/**
	 * Instantiate a HttpRequestBuilder.
	 * 
	 * @return HttpRequestBuilder instance.
	 */
	IHttpRequestBuilder createRequestBuilder();

	/**
	 * Instantiate a HttpResponseBuilder.
	 *
	 * @return HttpResponseBuilder instance.
	 */
	IHttpResponseBuilder createResponseBuilder();

	IHttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException;
	IHttpResponse sendRequest(HttpUriRequest request) throws IOException, ClientProtocolException;
	void registerResponseProcessor(IHttpResponseProcessor processor);
}
