package com.subgraph.vega.internal.model.conditions.match;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionStringMatchAction;

public abstract class StringMatchAction implements IHttpConditionMatchActionEx, IHttpConditionStringMatchAction {
	protected String stringValue;
	
	protected StringMatchAction() {}
	
	protected StringMatchAction(String value) {
		this.stringValue = value;
	}

	@Override
	public String getArgumentAsString() {
		return stringValue;
	}
	
	@Override
	public boolean setArgumentFromString(String value) {
		stringValue = value;
		return true;
	}

	@Override
	public boolean isValidArgumentString(String value) {
		return true;
	}

	protected boolean matchesContains(String value) {
		return value.contains(stringValue);
	}
	
	@Override
	public void setString(String value) {
		this.stringValue = value;
	}
}
