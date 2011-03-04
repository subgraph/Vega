package com.subgraph.vega.api.http.proxy;

public enum HttpInterceptorBreakpointType {
	DOMAIN_NAME("domain name"),
	REQUEST_METHOD("request method"),
	HEADER("header");

	private final String name;

	private HttpInterceptorBreakpointType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
