package com.subgraph.vega.internal.model.conditions.match;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class StringMatchActionSet implements IMatchActionSet {

	@Override
	public List<IHttpConditionMatchAction> createMatchActions() {
		final List<IHttpConditionMatchAction> actions = new ArrayList<IHttpConditionMatchAction>();
		actions.add(new ContainsAction());
		actions.add(new DoesNotContainAction());
		actions.add(new MatchesRegexAction());
		actions.add(new DoesNotMatchRegexAction());
		return actions;
	}
}
