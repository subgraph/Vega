package com.subgraph.vega.api.model.conditions;

import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IHttpConditionSet {
	boolean matches(HttpRequest request, HttpResponse response);
	void appendCondition(IHttpCondition condition);
	void removeCondition(IHttpCondition condition);
	List<IHttpCondition> getAllConditions();
	IHttpConditionManager getConditionManager();
}
