package com.subgraph.vega.api.model.conditions;

import java.util.List;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public interface IHttpConditionType {
	String getName();
	IHttpCondition createConditionInstance(IHttpConditionMatchAction matchAction);
	List<IHttpConditionMatchAction> getMatchActions();
}
