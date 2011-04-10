package com.subgraph.vega.api.model.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public interface IHttpCondition {
	IHttpConditionType getType();
	IHttpConditionMatchAction getMatchAction();
	void setMatchAction(IHttpConditionMatchAction matchAction);
	IHttpCondition createCopy();
	String getValueString();
	boolean matches(HttpRequest request);
	boolean matches(HttpResponse response);
	boolean matches(HttpRequest request, HttpResponse response);
	boolean isEnabled();
	void setEnabled(boolean state);
}
