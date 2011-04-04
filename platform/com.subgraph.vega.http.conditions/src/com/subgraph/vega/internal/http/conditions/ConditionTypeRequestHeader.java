package com.subgraph.vega.internal.http.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.MatchType;

public class ConditionTypeRequestHeader extends HttpBooleanCondition {
	Pattern rePattern;

	public ConditionTypeRequestHeader(Enum<?> comparisonType, String pattern, boolean isEnabled) {
		super(ConditionType.REQUEST_HEADER, comparisonType, pattern, isEnabled);
	}

	@Override
	public boolean test(HttpRequest request) {
		if (request != null) {
			MatchType matchType = (MatchType) comparisonType;
			for (HeaderIterator iterator = request.headerIterator(); iterator.hasNext();) {
				Header header = (Header) iterator.next();
				Matcher matcher = rePattern.matcher(header.toString());
				if (matcher.find() == (matchType == MatchType.MATCH)) {
					return true;
				}
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
