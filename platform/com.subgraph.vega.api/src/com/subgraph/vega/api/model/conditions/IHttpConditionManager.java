package com.subgraph.vega.api.model.conditions;

import java.util.List;

public interface IHttpConditionManager {
	IHttpConditionSet getConditionSet(String name);
	IHttpConditionSet getConditionSetCopy(String name);
	void saveConditionSet(String name, IHttpConditionSet conditionSet);
	IHttpConditionSet createConditionSet();
	List<IHttpConditionType> getConditionTypes();
}
