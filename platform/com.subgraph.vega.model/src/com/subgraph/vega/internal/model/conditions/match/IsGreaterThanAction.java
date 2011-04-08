package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class IsGreaterThanAction extends IntegerMatchAction {
	
	IsGreaterThanAction() {
	}
	
	private IsGreaterThanAction(int value) {
		super(value);
	}

	@Override
	public String getLabel() {
		return "is greater than";
	}

	@Override
	public void constrainQuery(Query query) {
		query.constrain(integerValue).greater();		
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new IsGreaterThanAction(integerValue);
	}

	@Override
	public boolean matchesValue(int value) {
		return value > integerValue;
	}
}
