package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;

/**
 * Specialized HttpServerConnection which overrides createHttpRequestFactor() to return
 * an HttpRequestFactory instance which can process the 'CONNECT' method for supporting
 * SSL interception.
 */
public class VegaHttpServerConnection extends DefaultHttpServerConnection {
	private HttpRequest cachedRequest;

	@Override
	protected HttpRequestFactory createHttpRequestFactory() {
        return new VegaHttpRequestFactory();
    }

	void setCachedRequest(HttpRequest request) {
		cachedRequest = request;
	}

	@Override
	public HttpRequest receiveRequestHeader() throws HttpException, IOException {
		if(cachedRequest != null) {
			final HttpRequest result = cachedRequest;
			cachedRequest = null;
			return result;
		}
		return super.receiveRequestHeader();
	}
}
