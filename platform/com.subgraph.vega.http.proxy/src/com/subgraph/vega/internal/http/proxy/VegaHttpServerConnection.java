package com.subgraph.vega.internal.http.proxy;

import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;

/**
 * Specialized HttpServerConnection which overrides createHttpRequestFactor() to return
 * an HttpRequestFactory instance which can process the 'CONNECT' method for supporting
 * SSL interception.
 */
public class VegaHttpServerConnection extends DefaultHttpServerConnection {
	@Override
	protected HttpRequestFactory createHttpRequestFactory() {
        return new VegaHttpRequestFactory();
    }
}
