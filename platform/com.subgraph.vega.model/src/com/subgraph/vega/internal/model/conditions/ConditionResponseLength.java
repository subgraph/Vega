package com.subgraph.vega.internal.model.conditions;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.internal.model.conditions.match.IntegerMatchActionSet;

public class ConditionResponseLength extends AbstractCondition {
	

	private static transient IHttpConditionType conditionType;
	
	static IHttpConditionType getConditionType() {
		synchronized (ConditionResponseLength.class) {
			if(conditionType == null)
				conditionType = createType();
			return conditionType;
		}
	}

	private static IHttpConditionType createType() {
		return new ConditionType("response length", new IntegerMatchActionSet()) {
			@Override
			public IHttpCondition createConditionInstance(IHttpConditionMatchAction matchAction) {
				return new ConditionResponseLength(matchAction);
			}
		};
	}

	private ConditionResponseLength(IHttpConditionMatchAction matchAction) {
		super(matchAction);
	}

	@Override
	public boolean matches(HttpRequest request) {
		return false;
	}

	@Override
	public boolean matches(HttpResponse response) {
		final int length = (int) getLengthFromResponse(response);
		return matchesInteger(length);
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
		return getConditionType();
	}

	@Override
	public void filterRequestLogQuery(Query query) {
		constrainQuery(query.descend("responseLength"));		
	}
}
