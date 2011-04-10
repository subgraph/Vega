package com.subgraph.vega.api.http.proxy;

import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;


public interface IHttpInterceptor {
	void setEventHandler(IHttpInterceptorEventHandler eventHandler);
	void setInterceptLevel(TransactionDirection direction, HttpInterceptorLevel level);
	HttpInterceptorLevel getInterceptLevel(TransactionDirection direction);
	int transactionQueueSize();
	IProxyTransaction transactionQueuePop();
}
