package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class DoesNotEndWithAction extends StringMatchAction {

	public DoesNotEndWithAction() {
		
	}

	private DoesNotEndWithAction(String value) {
		super(value);
	}
	
	@Override
	public Constraint constrainQuery(Query query) {
		return query.constrain(stringValue).endsWith(true).not();
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new DoesNotEndWithAction(stringValue);
	}

	@Override
	public String getLabel() {
		return "does not end with";
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_STRING;
	}

	@Override
	public boolean matchesValue(String value) {
		if(value == null) {
			return true;
		}
		return !value.endsWith(stringValue);
	}
}
