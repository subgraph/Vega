package com.subgraph.vega.internal.http.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.MatchType;

public class ConditionTypeRequestMethod extends HttpBooleanCondition {
	Pattern rePattern;

	public ConditionTypeRequestMethod(Enum<?> comparisonType, String pattern, boolean isEnabled) {
		super(ConditionType.REQUEST_METHOD, comparisonType, pattern, isEnabled);
	}

	@Override
	public boolean test(HttpRequest request) {
		if (request != null) {
			Matcher matcher = rePattern.matcher(request.getRequestLine().getMethod());
			if (matcher.find() == ((MatchType)comparisonType == MatchType.MATCH)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean test(HttpResponse response) {
		return false;
	}

	@Override
	void notifyPatternChange() {
		rePattern = Pattern.compile(pattern);
	}

}
