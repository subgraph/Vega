package com.subgraph.vega.api.model.conditions;

public interface IHttpRegexCondition extends IHttpCondition {
	void setPattern(String pattern);
	String getPattern();
}
