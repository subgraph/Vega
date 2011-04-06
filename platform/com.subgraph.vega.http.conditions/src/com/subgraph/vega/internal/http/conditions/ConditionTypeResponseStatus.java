package com.subgraph.vega.internal.http.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.MatchType;

public class ConditionTypeResponseStatus extends HttpBooleanCondition {
	Pattern rePattern;

	public ConditionTypeResponseStatus(Enum<?> comparisonType, String pattern, boolean isEnabled) {
		super(ConditionType.RESPONSE_STATUS, comparisonType, pattern, isEnabled);
	}

	@Override
	public boolean test(HttpRequest request) {
		return false;
	}

	@Override
	public boolean test(HttpResponse response) {
		if (response != null) {
			Matcher matcher = rePattern.matcher(Integer.toString(response.getStatusLine().getStatusCode()));
			if (matcher.find() == ((MatchType)comparisonType == MatchType.MATCH)) {
				return true;
			}
		}
		return false;
	}

	@Override
	void notifyPatternChange() {
		rePattern = Pattern.compile(pattern);
	}

}
