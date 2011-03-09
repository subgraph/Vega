package com.subgraph.vega.api.http.proxy;

public enum HttpInterceptorLevel {
	DISABLED("disabled"),
	ENABLED_ALL("all requests"),
	ENABLED_BREAKPOINTS("breakpoints");

	private final String name;

	private HttpInterceptorLevel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
