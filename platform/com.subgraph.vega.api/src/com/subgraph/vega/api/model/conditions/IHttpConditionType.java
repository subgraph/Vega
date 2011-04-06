package com.subgraph.vega.api.model.conditions;

public interface IHttpConditionType {
	enum HttpConditionStyle { CONDITION_REGEX, CONDITION_RANGE };
	String getName();
	HttpConditionStyle getStyle();
	IHttpCondition createConditionInstance();
}
