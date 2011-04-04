package com.subgraph.vega.internal.http.conditions;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.IHttpBooleanCondition;

abstract class HttpBooleanCondition implements IHttpBooleanCondition {
	private ConditionType conditionType;
	protected Enum<?> comparisonType;
	protected boolean isEnabled;
	protected String pattern;

	HttpBooleanCondition(ConditionType conditionType, Enum<?> comparisonType, String pattern, boolean isEnabled) {
		if (conditionType.getComparisonTypeClass() != comparisonType.getClass()) {
			throw new IllegalArgumentException("Comparison type is not valid for condition type");
		}
		this.conditionType = conditionType;
		this.comparisonType = comparisonType;
		this.pattern = pattern;
		this.isEnabled = isEnabled;
		notifyPatternChange();
	}
	
	@Override
	public ConditionType getType() {
		return conditionType;
	}

	@Override
	public void setComparisonType(Enum<?> comparisonType) {
		if (conditionType.getComparisonTypeClass() != comparisonType.getClass()) {
			throw new IllegalArgumentException("Comparison type is not valid for condition type");
		}
		this.comparisonType = comparisonType;
	}

	@Override
	public Enum<?> getComparisonType() {
		return comparisonType;
	}

	@Override
	public void setPattern(String pattern) {
		this.pattern = pattern;
		notifyPatternChange();
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return isEnabled;
	}

	abstract void notifyPatternChange();
}
