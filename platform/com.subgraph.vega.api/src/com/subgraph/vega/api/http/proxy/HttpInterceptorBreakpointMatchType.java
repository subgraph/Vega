package com.subgraph.vega.api.http.proxy;

public enum HttpInterceptorBreakpointMatchType {
	MATCH("matches"),
	NO_MATCH("does not match");

	private final String name;

	private HttpInterceptorBreakpointMatchType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
