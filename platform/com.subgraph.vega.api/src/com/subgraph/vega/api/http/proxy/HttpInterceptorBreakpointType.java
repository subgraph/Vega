package com.subgraph.vega.api.http.proxy;

public enum HttpInterceptorBreakpointType {
	DOMAIN_NAME("domain name", ProxyTransactionDirection.DIRECTION_REQUEST.getMask() | ProxyTransactionDirection.DIRECTION_RESPONSE.getMask()),
	REQUEST_METHOD("request method", ProxyTransactionDirection.DIRECTION_REQUEST.getMask() | ProxyTransactionDirection.DIRECTION_RESPONSE.getMask()),
	REQUEST_HEADER("request header", ProxyTransactionDirection.DIRECTION_REQUEST.getMask() | ProxyTransactionDirection.DIRECTION_RESPONSE.getMask()),
	RESPONSE_HEADER("response header", ProxyTransactionDirection.DIRECTION_RESPONSE.getMask());

	private final String name;
	private final int mask;

	private HttpInterceptorBreakpointType(String name, int mask) {
		this.name = name;
		this.mask = mask;
	}

	public String getName() {
		return name;
	}
	
	public int getMask() {
		return mask;
	}
}
