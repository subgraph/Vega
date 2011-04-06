package com.subgraph.vega.api.http.conditions;

public enum ConditionType {
	DOMAIN_NAME("domain name", MatchType.class,
			TransactionDirection.DIRECTION_REQUEST.getMask() | TransactionDirection.DIRECTION_RESPONSE.getMask()),
	REQUEST_METHOD("request method", MatchType.class,
			TransactionDirection.DIRECTION_REQUEST.getMask() | TransactionDirection.DIRECTION_RESPONSE.getMask()),
	REQUEST_HEADER("request header", MatchType.class,
			TransactionDirection.DIRECTION_REQUEST.getMask() | TransactionDirection.DIRECTION_RESPONSE.getMask()),
	RESPONSE_STATUS("status code", MatchType.class,
			TransactionDirection.DIRECTION_RESPONSE.getMask()),
	RESPONSE_HEADER("response header", MatchType.class,
			TransactionDirection.DIRECTION_RESPONSE.getMask());

	private final String name;
	private Class<?> comparisonTypeClass;
	private final int mask;

	private ConditionType(String name, Class<?> comparisonTypeClass, int mask) {
		this.name = name;
		this.comparisonTypeClass = comparisonTypeClass;
		this.mask = mask;
	}

	public String getName() {
		return name;
	}

	public Class<?> getComparisonTypeClass() {
		return comparisonTypeClass;
	}

	public int getMask() {
		return mask;
	}

}
