package com.subgraph.vega.api.model.conditions.match;

public interface IHttpConditionRangeMatchAction extends IHttpConditionMatchAction {
	void setRange(int rangeLow, int rangeHigh);
	boolean matchesValue(int value);
}
