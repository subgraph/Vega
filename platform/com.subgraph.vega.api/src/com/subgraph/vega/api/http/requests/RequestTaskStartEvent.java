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
package com.subgraph.vega.api.http.requests;

import com.subgraph.vega.api.events.IEvent;

/**
 * Event indicating an IHttpRequestTask has begun performing an HTTP request.
 */
public class RequestTaskStartEvent implements IEvent {
	private final IHttpRequestTask requestTask;
	
	public RequestTaskStartEvent(IHttpRequestTask requestTask) {
		this.requestTask = requestTask;
	}

	public IHttpRequestTask getRequestTask() {
		return requestTask;
	}
	
}
