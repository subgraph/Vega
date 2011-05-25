package com.subgraph.vega.api.http.proxy;

import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;

/**
 * When a transaction is added to the queue, event handlers registered with the interceptor are notified. Transactions
 * are removed from the queue by either forwarding or dropping them individually. 
 */
public interface IHttpInterceptor {
	void addEventHandler(IHttpInterceptorEventHandler eventHandler);
	void removeEventHandler(IHttpInterceptorEventHandler eventHandler);
	void setInterceptLevel(TransactionDirection direction, HttpInterceptorLevel level);
	HttpInterceptorLevel getInterceptLevel(TransactionDirection direction);
	int transactionQueueSize();
	IProxyTransaction[] getTransactions();
	IProxyTransaction transactionQueueGet(int idx);	
}
