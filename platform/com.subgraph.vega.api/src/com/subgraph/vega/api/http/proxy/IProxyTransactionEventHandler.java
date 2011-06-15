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
