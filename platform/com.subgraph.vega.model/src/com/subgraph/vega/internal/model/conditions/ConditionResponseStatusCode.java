package com.subgraph.vega.internal.model.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.IHttpConditionType.HttpConditionStyle;

public class ConditionResponseStatusCode extends AbstractRangeCondition {
	static private transient IHttpConditionType conditionType;
	
	static IHttpConditionType getConditionType() {
		synchronized(ConditionResponseStatusCode.class) {
			if(conditionType == null)
				conditionType = createType();
			return conditionType;
		}
	}

	private static IHttpConditionType createType() {
		return new ConditionType("status code", HttpConditionStyle.CONDITION_RANGE) {			
			@Override
			public IHttpCondition createConditionInstance() {
				return new ConditionResponseStatusCode();
			}
		};
	}

	@Override
	public boolean matches(HttpRequest request) {
		return false;
	}

	@Override
	public boolean matches(HttpResponse response) {
		if(response == null)
			return false;
		return maybeInvert(matchesRange(response.getStatusLine().getStatusCode()));
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
		constrainQuery(query.descend("responseCode"));		
	}
}
