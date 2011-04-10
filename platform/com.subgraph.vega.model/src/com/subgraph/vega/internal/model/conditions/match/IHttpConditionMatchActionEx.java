package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public interface IHttpConditionMatchActionEx extends IHttpConditionMatchAction {
	void constrainQuery(Query query);
	IHttpConditionMatchAction createCopy();
}
