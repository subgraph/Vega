package com.subgraph.vega.api.http.proxy;

public enum HttpInterceptorLevel {
	DISABLED("disabled", -1),
	ENABLED_ALL("all transactions", 1),
	ENABLED_BREAKPOINTS("breakpoints", 0);

	private final String name;
	private final Integer serializeValue;
	
	private HttpInterceptorLevel(String name, Integer serializeValue) {
		this.name = name;
		this.serializeValue = serializeValue;
	}

	public String getName() {
		return name;
	}

	public Integer getSerializeValue() {
		return serializeValue;
	}

	public static HttpInterceptorLevel fromValue(Integer serializeValue) {
		if (serializeValue != null) {
			for (HttpInterceptorLevel level: values()) {  
				if (level.serializeValue.compareTo(serializeValue) == 0) {  
					return level;  
				}
			}
		}
		return null;
	}

	public static HttpInterceptorLevel getDefault() {
		return DISABLED;
	}

}
