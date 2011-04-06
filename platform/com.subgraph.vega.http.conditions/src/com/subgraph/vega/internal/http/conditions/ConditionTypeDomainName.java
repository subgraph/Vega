package com.subgraph.vega.internal.http.conditions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.MatchType;

public class ConditionTypeDomainName extends HttpBooleanCondition {
	Pattern rePattern;

	public ConditionTypeDomainName(Enum<?> comparisonType, String pattern, boolean isEnabled) {
		super(ConditionType.DOMAIN_NAME, comparisonType, pattern, isEnabled);
	}

	@Override
	public boolean test(HttpRequest request) {
		if (request != null) {
			// REVISIT: parsing the URI every time we process this condition is a waste
			URI uri;
			try {
				uri = new URI(request.getRequestLine().getUri());
			} catch (URISyntaxException e) {
				return false;
			}
			Matcher matcher = rePattern.matcher(uri.getHost());
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
