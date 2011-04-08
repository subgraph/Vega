package com.subgraph.vega.api.model.conditions.match;

public interface IHttpConditionMatchAction {
	enum MatchActionArgumentType { ARGUMENT_REGEX, ARGUMENT_STRING, ARGUMENT_INTEGER, ARGUMENT_RANGE };
	
	String getLabel();
	MatchActionArgumentType getArgumentType();
	String getArgumentAsString();
	boolean setArgumentFromString(String value);
	boolean isValidArgumentString(String value);
}

