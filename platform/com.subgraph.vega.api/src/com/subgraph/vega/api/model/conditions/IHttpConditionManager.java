package com.subgraph.vega.api.model.conditions;

import java.util.List;

public interface IHttpConditionManager {
	public static final String CONDITION_SET_FILTER = "filter";
	public static final String CONDITION_SET_BREAKPOINTS_REQUEST = "breakpoints-request";
	public static final String CONDITION_SET_BREAKPOINTS_RESPONSE = "breakpoints-response";
	IHttpConditionSet getConditionSet(String name);
	IHttpConditionSet getConditionSetCopy(String name);
	void saveConditionSet(String name, IHttpConditionSet conditionSet);
	List<IHttpConditionType> getConditionTypes();
}
