package com.subgraph.vega.internal.model.conditions;

import java.util.List;

import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.internal.model.conditions.match.IMatchActionSet;

public abstract class ConditionType implements IHttpConditionType {

	private final String name;
	private final IMatchActionSet matchActionSet;
	
	ConditionType(String name, IMatchActionSet matchActionSet) {
		this.name = name;
		this.matchActionSet = matchActionSet;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public List<IHttpConditionMatchAction> getMatchActions() {
		return matchActionSet.createMatchActions();
	}
	
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof ConditionType) {
			ConditionType that = (ConditionType) other;
			return this.name.equals(that.name);
		}
		return false;
	}
	
	public int HashCode() {
		return name.hashCode();
	}
}
