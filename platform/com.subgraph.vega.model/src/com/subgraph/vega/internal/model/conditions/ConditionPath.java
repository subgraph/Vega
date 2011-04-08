package com.subgraph.vega.internal.model.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.internal.model.conditions.match.StringMatchActionSet;

public class ConditionPath extends AbstractCondition {

	static private transient IHttpConditionType conditionType;
	
	static IHttpConditionType getConditionType() {
		synchronized(ConditionPath.class) {
			if(conditionType == null)
				conditionType = createType();
			return conditionType;
		}
	}
	
	private static IHttpConditionType createType() {
		return new ConditionType("request path", new StringMatchActionSet()) {
			@Override
			public IHttpCondition createConditionInstance(IHttpConditionMatchAction matchAction) {
				return new ConditionPath(matchAction);
			}			
		};
	}

	private ConditionPath(IHttpConditionMatchAction matchAction) {
		super(matchAction);
	}

	@Override
	public IHttpConditionType getType() {
		return getConditionType();
	}

	@Override
	public boolean matches(HttpRequest request) {
		return matchesString(request.getRequestLine().getUri());
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
	public void filterRequestLogQuery(Query query) {
		constrainQuery(query.descend("requestPath"));		
	}

}
