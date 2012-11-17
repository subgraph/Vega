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
package com.subgraph.vega.internal.http.requests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;

public class HttpRequestEngineConfig implements IHttpRequestEngineConfig {
	private final static int DEFAULT_REQUESTS_PER_MINUTE = 1000;
	private boolean forceIdentityEncoding = false;
	private boolean decompressGzipEncoding = true;
	private boolean undoURLEncoding = false;
	private int requestsPerMinute = DEFAULT_REQUESTS_PER_MINUTE;
	private int maxConnections = DEFAULT_MAX_CONNECTIONS;
	private int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
	private int maximumResponseKilobytes = 0; // 0 means no limit
	private final List<IHttpResponseProcessor> responseProcessors = new ArrayList<IHttpResponseProcessor>();
	
	@Override
	public void setForceIdentityEncoding(boolean value) {
		forceIdentityEncoding = value;		
	}

	@Override
	public void setDecompressGzipEncoding(boolean value) {
		decompressGzipEncoding = value;		
	}

	@Override
	public boolean getForceIdentityEncoding() {
		return forceIdentityEncoding;
	}

	@Override
	public boolean getDecompressGzipEncoding() {
		return decompressGzipEncoding;
	}

	@Override
	public void setUndoURLEncoding(boolean value) {
		undoURLEncoding = value;
	}

	@Override
	public boolean getUndoURLEncoding() {
		return undoURLEncoding;
	}

	@Override
	public void registerResponseProcessor(IHttpResponseProcessor processor) {
		synchronized(responseProcessors) {
			responseProcessors.add(processor);
		}
	}

	@Override
	public List<IHttpResponseProcessor> getResponseProcessors() {
		synchronized(responseProcessors) {
			return Collections.unmodifiableList(responseProcessors);
		}
	}

	@Override
	public void setRequestsPerMinute(int rpm) {
		requestsPerMinute = rpm;		
	}

	@Override
	public int getRequestsPerMinute() {
		return requestsPerMinute;
	}

	@Override
	public void setMaxConnections(int value) {
		maxConnections = value;
	}

	@Override
	public int getMaxConnections() {
		return maxConnections;
	}

	@Override
	public void setMaxConnectionsPerRoute(int value) {
		maxConnectionsPerRoute = value;
	}

	@Override
	public int getMaxConnectionsPerRoute() {
		return maxConnectionsPerRoute;
	}

	@Override
	public void setMaximumResponseKilobytes(int kb) {
		maximumResponseKilobytes = kb;
	}

	@Override
	public int getMaximumResponseKilobytes() {
		return maximumResponseKilobytes;
	}
}
