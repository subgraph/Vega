package com.subgraph.vega.api.model.conditions;

import java.util.List;

import com.subgraph.vega.api.events.IEventHandler;

public interface IHttpConditionManager {
	IHttpConditionSet getConditionSet(String name);
	IHttpConditionSet getConditionSetCopy(String name);
	void saveConditionSet(String name, IHttpConditionSet conditionSet);
	IHttpConditionSet createConditionSet();
	List<IHttpConditionType> getConditionTypes();
	void addConditionSetListenerByName(String name, IEventHandler listener);
	void removeConditionSetListener(IEventHandler listener);
}
