/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.api.http.proxy;

import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;

/**
 * When a transaction is added to the queue, event handlers registered with the interceptor are notified. Transactions
 * are removed from the queue by either forwarding or dropping them individually. 
 */
public interface IHttpInterceptor {
	void setEnabled(boolean enabled);
	boolean isEnabled();
	void addEventHandler(IHttpInterceptorEventHandler eventHandler);
	void removeEventHandler(IHttpInterceptorEventHandler eventHandler);
	void setInterceptLevel(TransactionDirection direction, HttpInterceptorLevel level);
	HttpInterceptorLevel getInterceptLevel(TransactionDirection direction);
	int transactionQueueSize();
	IProxyTransaction[] getTransactions();
	IProxyTransaction transactionQueueGet(int idx);	
}
