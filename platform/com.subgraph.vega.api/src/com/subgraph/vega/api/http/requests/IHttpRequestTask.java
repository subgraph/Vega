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

import java.util.Date;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Represents a task to perform a HTTP request using the request engine. 
 */
public interface IHttpRequestTask {
	/**
	 * Get the request engine managing this task.
	 * @return Request engine.
	 */
	IHttpRequestEngine getRequestEngine();

	/**
	 * Get the request being executed.
	 * @return Request.
	 */
	HttpUriRequest getRequest();

	/**
	 * Abort the request.
	 * @throws UnsupportedOperationException
	 */
	void abort() throws UnsupportedOperationException;
	
	/**
	 * Get the result of the request. Blocks until the request result is ready.
	 * @return Response.
	 * @throws RequestEngineException 
	 */
	IHttpResponse get(boolean readEntity) throws RequestEngineException;

	/**
	 * Determine whether the request task has completed.
	 * @return Boolean indicating whether the request task has completed. 
	 */
	boolean isComplete();
	
	/**
	 * Get the time the request task finished performing the request.
	 * @return Completion time, or null.
	 */
	Date getTimeCompleted();
}
