package com.subgraph.vega.internal.model.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.IHttpConditionType.HttpConditionStyle;

public class ConditionRequestMethod extends AbstractRegexCondition {
	static private transient IHttpConditionType conditionType;
	
	static IHttpConditionType getConditionType() {
		synchronized(ConditionRequestMethod.class) {
			if(conditionType == null)
				conditionType = createType();
			return conditionType;
		}
	}
	
	private static IHttpConditionType createType() {
		return new ConditionType("request method", HttpConditionStyle.CONDITION_REGEX) {
			@Override
			public IHttpCondition createConditionInstance() {
				return new ConditionRequestMethod();
			}
		};
	}

	@Override
	public boolean matches(HttpRequest request) {
		if(request == null)
			return false;
		final String method = request.getRequestLine().getMethod();
		return maybeInvert(matchesPattern(method));
	}

	@Override
	public boolean matches(HttpResponse response) {
		return false;
	}

	@Override
	public boolean matches(HttpRequest request, HttpResponse response) {
		return matches(request);
	}

	@Override
	public IHttpConditionType getType() {
		return getConditionType();
	}

	@Override
	public void filterRequestLogQuery(Query query) {
		// TODO Auto-generated method stub
		
	}	
}
