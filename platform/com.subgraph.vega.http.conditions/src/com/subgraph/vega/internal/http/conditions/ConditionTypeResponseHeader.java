package com.subgraph.vega.internal.http.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.MatchType;

public class ConditionTypeResponseHeader extends HttpBooleanCondition {
	Pattern rePattern;

	public ConditionTypeResponseHeader(Enum<?> comparisonType, String pattern, boolean isEnabled) {
		super(ConditionType.RESPONSE_HEADER, comparisonType, pattern, isEnabled);
	}

	@Override
	public boolean test(HttpRequest request) {
		return false;
	}

	@Override
	public boolean test(HttpResponse response) {
		if (response != null) {
			MatchType matchType = (MatchType) comparisonType;
			for (HeaderIterator iterator = response.headerIterator(); iterator.hasNext();) {
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
	void notifyPatternChange() {
		rePattern = Pattern.compile(pattern);
	}

}
