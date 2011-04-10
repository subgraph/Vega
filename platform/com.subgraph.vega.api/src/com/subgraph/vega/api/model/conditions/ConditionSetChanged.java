package com.subgraph.vega.api.model.conditions;

import com.subgraph.vega.api.events.IEvent;

public class ConditionSetChanged implements IEvent {
	private final IHttpConditionSet conditionSet;

	public ConditionSetChanged(IHttpConditionSet conditionSet) {
		this.conditionSet = conditionSet;
	}

	public IHttpConditionSet getConditionSet() {
		return conditionSet;
	}
}
