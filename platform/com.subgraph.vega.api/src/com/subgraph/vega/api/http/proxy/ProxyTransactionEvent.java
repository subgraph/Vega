package com.subgraph.vega.api.http.proxy;

import com.subgraph.vega.api.events.IEvent;

public class ProxyTransactionEvent implements IEvent {
	private final IProxyTransaction transaction;
	
	public ProxyTransactionEvent(IProxyTransaction transaction) {
		this.transaction = transaction;
	}

	public IProxyTransaction getTransaction() {
		return transaction;
	}
}
