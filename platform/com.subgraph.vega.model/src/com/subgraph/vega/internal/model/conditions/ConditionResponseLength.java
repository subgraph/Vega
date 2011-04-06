package com.subgraph.vega.internal.model.conditions;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.IHttpConditionType.HttpConditionStyle;

public class ConditionResponseLength extends AbstractRangeCondition {

	static IHttpConditionType createType() {
		return new ConditionType("response length", HttpConditionStyle.CONDITION_RANGE) {
			@Override
			public IHttpCondition createConditionInstance() {
				return new ConditionResponseLength();
			}
		};
	}

	@Override
	public boolean matches(HttpRequest request) {
		return false;
	}

	@Override
	public boolean matches(HttpResponse response) {
		final int length = (int) getLengthFromResponse(response);
		return maybeInvert(matchesRange(length));
	}

	private long getLengthFromResponse(HttpResponse response) {
		final Header lengthHeader = response.getFirstHeader("Content-Length");
		if(lengthHeader != null) {
			try {
				return Long.parseLong(lengthHeader.getValue());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		if(response.getEntity() == null)
			return 0;
		return response.getEntity().getContentLength();
	}
	
	@Override
	public boolean matches(HttpRequest request, HttpResponse response) {
		return matches(response);
	}

	@Override
	public IHttpConditionType getType() {
		return createType();
	}
}
