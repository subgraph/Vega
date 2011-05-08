package com.subgraph.vega.api.model.conditions;

import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IHttpConditionSet {
	String getName();
	boolean matches(HttpRequest request, HttpResponse response);
	void appendCondition(IHttpCondition condition);
	void removeCondition(IHttpCondition condition);
	void clearConditions();
	List<IHttpCondition> getAllConditions();
	IHttpConditionManager getConditionManager();
	void setMatchOnEmptySet(boolean flag);
}
