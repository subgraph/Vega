package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class IsLessThanAction extends IntegerMatchAction {
	
	IsLessThanAction() {
	}
	
	private IsLessThanAction(int value) {
		super(value);
	}
	
	@Override
	public String getLabel() {
		return "is less than";
	}

	@Override
	public boolean matchesValue(int value) {
		return value < integerValue;
	}

	@Override
	public void constrainQuery(Query query) {
		query.constrain(integerValue).smaller();
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new IsLessThanAction(integerValue);
	}
}
