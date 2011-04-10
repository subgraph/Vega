package com.subgraph.vega.internal.model.conditions.match;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class IntegerMatchActionSet implements IMatchActionSet {
	@Override
	public List<IHttpConditionMatchAction> createMatchActions() {
		final List<IHttpConditionMatchAction> actions = new ArrayList<IHttpConditionMatchAction>();
		actions.add(new EqualsAction());
		actions.add(new IsGreaterThanAction());
		actions.add(new IsLessThanAction());
		actions.add(new IsInsideRangeAction());
		return actions;
	}
}
