package com.subgraph.vega.internal.model.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.internal.model.conditions.match.IntegerMatchActionSet;

public class ConditionRequestId extends AbstractCondition {

	private transient static IHttpConditionType conditionType;
	
	static IHttpConditionType getConditionType() {
		synchronized (ConditionRequestId.class) {
			if(conditionType == null) {
				conditionType = createType();
			}
			return conditionType;
		}
	}
	
	private static IHttpConditionType createType() {
		return new ConditionType("request id", new IntegerMatchActionSet(), true) {
			@Override
			public IHttpCondition createConditionInstance(
					IHttpConditionMatchAction matchAction) {
				return new ConditionRequestId(matchAction);
			}
		};
	}
	private ConditionRequestId(IHttpConditionMatchAction matchAction) {
		super(matchAction);
	}

	@Override
	public IHttpConditionType getType() {
		return getConditionType();
	}
	
	@Override
	public boolean matches(IRequestLogRecord record) {
		return matchesInteger((int) record.getRequestId());
	}
	
	@Override
	public boolean matches(HttpRequest request) {
		return false;
	}

	@Override
	public boolean matches(HttpResponse response) {
		return false;
	}

	@Override
	public boolean matches(HttpRequest request, HttpResponse response) {
		return false;
	}

	@Override
	public boolean isInternal() {
		return true;
	}

	@Override
	public Constraint filterRequestLogQuery(Query query) {
		return constrainQuery(query.descend("requestId"));
	}
}
