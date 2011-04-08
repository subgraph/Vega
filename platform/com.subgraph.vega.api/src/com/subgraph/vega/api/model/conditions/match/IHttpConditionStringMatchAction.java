package com.subgraph.vega.api.model.conditions.match;

public interface IHttpConditionStringMatchAction extends IHttpConditionMatchAction {
	void setString(String value);
	boolean matchesValue(String value);
}
