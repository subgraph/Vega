package com.subgraph.vega.api.http.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IHttpConditionSet {
	IHttpBooleanCondition createCondition(ConditionType conditionType, Enum<?> comparisonType, String pattern, boolean isEnabled);
	void removeCondition(IHttpBooleanCondition condition);
	int getBreakpontIdxOf(IHttpBooleanCondition condition);
	int getConditionCnt();
	IHttpBooleanCondition[] getConditions();
	String serialize();
	void unserialize(String str);
	boolean test(HttpRequest request, HttpResponse response);
}
