package com.subgraph.vega.api.http.proxy;

import com.subgraph.vega.api.http.conditions.IHttpConditionSet;
import com.subgraph.vega.api.http.conditions.TransactionDirection;

public interface IHttpInterceptor {
	void setEventHandler(IHttpInterceptorEventHandler eventHandler);
	void setInterceptLevel(TransactionDirection direction, HttpInterceptorLevel level);
	HttpInterceptorLevel getInterceptLevel(TransactionDirection direction);
	IHttpConditionSet getBreakpointSet(TransactionDirection direction);
	int transactionQueueSize();
	IProxyTransaction transactionQueuePop();
}
