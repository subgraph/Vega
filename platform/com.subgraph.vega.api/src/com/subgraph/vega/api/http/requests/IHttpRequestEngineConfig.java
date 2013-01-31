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

import java.util.List;

public interface IHttpRequestEngineConfig {
	final static int DEFAULT_MAX_CONNECTIONS = 25;
	final static int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 25;
	void setForceIdentityEncoding(boolean value);
	void setDecompressGzipEncoding(boolean value);
	void setUndoURLEncoding(boolean value);
	boolean getForceIdentityEncoding();
	boolean getDecompressGzipEncoding();
	boolean getUndoURLEncoding();
	void registerResponseProcessor(IHttpResponseProcessor processor);
	List<IHttpResponseProcessor> getResponseProcessors();
	void setRequestsPerMinute(int rpm);
	int getRequestsPerMinute();
	void setMaxConnections(int value);
	int getMaxConnections();
	void setMaxConnectionsPerRoute(int value);
	int getMaxConnectionsPerRoute();
	void setMaximumResponseKilobytes(int kb);
	int getMaximumResponseKilobytes();
}
