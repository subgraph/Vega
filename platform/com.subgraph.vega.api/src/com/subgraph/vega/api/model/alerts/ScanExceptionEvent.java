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
package com.subgraph.vega.api.model.alerts;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.events.IEvent;

public class ScanExceptionEvent implements IEvent {
	
	private final HttpUriRequest request;
	private final Throwable exception;
	
	
	public ScanExceptionEvent(HttpUriRequest request, Throwable exception) {
		this.request = request;
		this.exception = exception;
	}
	
	public HttpUriRequest getRequest() {
		return request;
	}
	
	public Throwable getException() {
		return exception;
	}
}
