package com.subgraph.vega.api.scanner.model;

public class AlertWebResource {
	private final String url;
	private final String description;
	
	public AlertWebResource(String url, String description) {
		this.url = url;
		this.description = description;
	}
	
	public String getURL() {
		return url;
	}

	public String getDescription() {
		return description;
	}
}
