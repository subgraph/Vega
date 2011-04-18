package com.subgraph.vega.internal.http.proxy.ssl;

public class ProxySSLInitializationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	ProxySSLInitializationException(String message) {
		super(message);
	}

	ProxySSLInitializationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
