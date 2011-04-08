package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class EqualsAction extends IntegerMatchAction {
		
	EqualsAction() {
		this(0);
	}
	
	private EqualsAction(int value) {
		super(value);
	}

	@Override
	public String getLabel() {
		return "equals";
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new EqualsAction(integerValue);
	}

	@Override
	public void constrainQuery(Query query) {
		query.constrain(integerValue).equal();
	}

	@Override
	public boolean matchesValue(int value) {
		return value == integerValue;
	}
}
