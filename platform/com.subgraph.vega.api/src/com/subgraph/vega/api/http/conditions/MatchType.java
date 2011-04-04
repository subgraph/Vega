package com.subgraph.vega.api.http.conditions;

public enum MatchType {
	MATCH("matches"),
	NO_MATCH("does not match");

	private final String name;

	private MatchType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
