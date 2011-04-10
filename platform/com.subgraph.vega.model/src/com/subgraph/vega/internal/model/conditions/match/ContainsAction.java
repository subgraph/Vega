package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class ContainsAction extends StringMatchAction {

	private ContainsAction(String value) {
		super(value);
	}

	public ContainsAction() {
	}

	@Override
	public String getLabel() {
		return "contains";
	}

	@Override
	public void constrainQuery(Query query) {
		query.constrain(stringValue).contains();
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new ContainsAction(stringValue);
	}

	@Override
	public boolean matchesValue(String value) {
		return matchesContains(value);
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_STRING;
	}
}
