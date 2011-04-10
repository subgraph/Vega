package com.subgraph.vega.internal.model.conditions.match;

import java.util.List;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public interface IMatchActionSet {
	List<IHttpConditionMatchAction> createMatchActions();
}
