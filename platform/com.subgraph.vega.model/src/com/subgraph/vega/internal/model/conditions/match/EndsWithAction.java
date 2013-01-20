package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class EndsWithAction extends StringMatchAction {

	public EndsWithAction() {
	}

	private EndsWithAction(String value) {
		super(value);
	}

	@Override
	public Constraint constrainQuery(Query query) {
		return query.constrain(stringValue).endsWith(true);
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new EndsWithAction(stringValue);
	}

	@Override
	public String getLabel() {
		return "ends with";
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_STRING;
	}

	@Override
	public boolean matchesValue(String value) {
		if(value == null) {
			return false;
		}
		return value.endsWith(stringValue);
	}
}
