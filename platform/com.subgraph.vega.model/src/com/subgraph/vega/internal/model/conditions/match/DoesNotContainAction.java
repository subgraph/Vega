package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class DoesNotContainAction extends StringMatchAction {
	
	private DoesNotContainAction(String value) {
		super(value);
	}

	public DoesNotContainAction() {
	}

	@Override
	public String getLabel() {
		return "does not contain";
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new DoesNotContainAction(stringValue);
	}

	@Override
	public void constrainQuery(Query query) {
		query.constrain(stringValue).contains().not();
	}

	@Override
	public boolean matchesValue(String value) {
		return !matchesContains(value);
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_STRING;
	}	
}
