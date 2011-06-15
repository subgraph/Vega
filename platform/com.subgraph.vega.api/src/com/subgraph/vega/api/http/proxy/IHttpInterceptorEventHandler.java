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

public interface IHttpInterceptorEventHandler {
	/**
	 * Notification that a transaction was queued for processing.
	 *
	 * @param transaction Transaction.
	 * @param idx Array index in queue transaction was added at.
	 */
	public void notifyQueue(IProxyTransaction transaction, int idx);

	/**
	 * Notification that a transaction was removed from the queue. Not invoked when the last item is removed form the
	 * queue, notifyEmpty() is invoked instead.
	 * 
	 * @param idx Array index of transaction that was removed. 
	 */
	public void notifyRemove(int idx);

	/**
	 * Notification that the transaction queue is empty.
	 */
	public void notifyEmpty();
}
