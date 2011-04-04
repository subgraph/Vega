package com.subgraph.vega.api.http.conditions;

public enum TransactionDirection {
	DIRECTION_REQUEST("request", (1 << 0)),
	DIRECTION_RESPONSE("response", (1 << 1));

	private final String name;
	private final int mask;

	private TransactionDirection(String name, int mask) {
		this.name = name;
		this.mask = mask;
	}

	public String getName() {
		return name;
	}
	
	public int getMask() {
		return mask;
	}
};
