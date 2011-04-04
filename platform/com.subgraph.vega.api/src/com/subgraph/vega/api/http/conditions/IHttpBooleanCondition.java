package com.subgraph.vega.api.http.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * Interface for a class to evaluate an HTTP transaction for a condition and return true or false.
 */  
public interface IHttpBooleanCondition {
	ConditionType getType();
	void setComparisonType(Enum<?> comparisonType) throws IllegalArgumentException;
	Enum<?> getComparisonType();
	void setPattern(String pattern);
	String getPattern();
	void setIsEnabled(boolean isEnabled);
	boolean getIsEnabled();
	boolean test(HttpRequest request);
	boolean test(HttpResponse response);
}
