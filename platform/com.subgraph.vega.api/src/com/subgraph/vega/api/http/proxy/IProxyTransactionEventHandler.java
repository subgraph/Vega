package com.subgraph.vega.api.http.proxy;

public interface IProxyTransactionEventHandler {
	/**
	 * Notification that the transaction is about to be forwarded. May indicate the request is about to be sent to the
	 * target server or that the response is about to be sent to the client.
	 */	
	void notifyForward();

	/**
	 * Notification that the transaction is complete.
	 * 
	 * @param dropped Boolean indicating whether the transaction was dropped.
	 */
	void notifyComplete(boolean dropped);
}
