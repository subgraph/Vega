package com.subgraph.vega.api.model.conditions;

public interface IHttpRangeCondition extends IHttpCondition {
	void setRangeLow(int low);
	void setRangeHigh(int high);
	int getRangeLow();
	int getRangeHigh();
}
