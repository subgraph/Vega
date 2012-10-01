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
package com.subgraph.vega.impl.scanner.state;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;

/*
 * This is a separate class out so that several 'argument' ModuleContext instances
 * can share a common mutable state. 
 */
public class ModuleContextState {
	private final static int INITIAL_CAPACITY = 8;

	private HttpUriRequest[] savedRequests;
	private IHttpResponse[] savedResponses;
	private int currentCapacity;
	private int responseCount;
	private int sentRequestCount;
	private boolean moduleFailed;

	public synchronized void incrementSentRequestCount() {
		sentRequestCount += 1;
	}
	
	public synchronized int incrementResponseCount() {
		responseCount += 1;
		return responseCount;
	}
	
	public synchronized boolean allResponsesReceieved() {
		return responseCount == sentRequestCount;
	}

	private void ensureCapacity(int index) {
		if(currentCapacity == 0) {
			currentCapacity = INITIAL_CAPACITY;
			savedRequests = new HttpUriRequest[INITIAL_CAPACITY];
			savedResponses = new IHttpResponse[INITIAL_CAPACITY];
		}
		
		if(index >= currentCapacity) {		
			final HttpUriRequest[] newRequests = new HttpUriRequest[index + 1];
			final IHttpResponse[] newResponses = new IHttpResponse[index + 1];
			System.arraycopy(savedRequests, 0, newRequests, 0, currentCapacity);
			System.arraycopy(savedResponses, 0, newResponses, 0, currentCapacity);
			savedRequests = newRequests;
			savedResponses = newResponses;
			currentCapacity = index + 1;
		}
	}

	public synchronized void addRequestResponse(int index, HttpUriRequest request, IHttpResponse response) {
		ensureCapacity(index);
		savedRequests[index] = request;
		savedResponses[index] = response;
	}
	
	public synchronized HttpUriRequest getSavedRequest(int index) {
		ensureCapacity(index);
		return savedRequests[index];
	}
	
	public synchronized IHttpResponse getSavedResponse(int index) {
		ensureCapacity(index);
		return savedResponses[index];
	}
	
	/* Added below method because of bug #547 */

	public synchronized String getSavedResponseBody(int index) {
		ensureCapacity(index);
		final IHttpResponse response = savedResponses[index];
		if(response != null)
			return response.getBodyAsString();
		return null;
	}
	public synchronized IPageFingerprint getSavedFingerprint(int index) {
		ensureCapacity(index);
		final IHttpResponse response = savedResponses[index];
		if(response != null)
			return response.getPageFingerprint();
		return null;
	}
	
	public synchronized void setModuleFailed() {
		moduleFailed = true;		
	}

	public synchronized boolean hasModuleFailed() {
		return moduleFailed;
	}
}
