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

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public interface IProxyTransaction {
	enum TransactionDirection {
		DIRECTION_REQUEST("request"),
		DIRECTION_RESPONSE("response");
		private final String name;
		TransactionDirection(String name) { this.name = name; }
		String getName() { return name; }
	};

	/**
	 * @return Request engine associated with the transaction.
	 */
	IHttpRequestEngine getRequestEngine();

	/**
	 * Set an event handler to receive a notification when the transaction completes.
	 *  
	 * @param eventHandler Proxy event handler, or null to unset.
	 */
	void setEventHandler(IProxyTransactionEventHandler eventHandler);

	/**
	 * Set the request to be sent by the proxy, overriding the intercepted request. If none is set when the pending
	 * request is forwarded, the intercepted request is sent as is.
	 *
	 * @param request HttpUriRequest to be sent by the proxy. 
	 */
	void setRequest(HttpUriRequest request);

	/**
	 * @return Boolean indicating whether this transaction has a HTTP request received by the proxy.
	 */
	boolean hasRequest();

	/**
	 * @return HTTP request received by the proxy. Immutable.
	 */
	HttpUriRequest getRequest();

	/**
	 * @return Boolean indicating whether this transaction has a HTTP response received by the proxy.
	 */
	boolean hasResponse();

	/**
	 * @return HTTP response received by the proxy.
	 */
	IHttpResponse getResponse();

	/**
	 * Forward the pending request or response. 
	 */
	void doForward();

	/**
	 * Drop the pending request or response.
	 */
	void doDrop();
}
