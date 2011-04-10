package com.subgraph.vega.api.model.conditions.match;

public interface IHttpConditionIntegerMatchAction extends IHttpConditionMatchAction {
	void setInteger(int value);
	boolean matchesValue(int value);
}
