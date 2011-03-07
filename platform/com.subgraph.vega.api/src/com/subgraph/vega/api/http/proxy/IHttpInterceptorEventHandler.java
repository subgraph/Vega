package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptorEventHandler {
	/**
	 * Notification that a transaction was queued for processing.
	 *
	 * @param transaction Transaction.
	 */
	public void notifyQueue(IProxyTransaction transaction);
}
