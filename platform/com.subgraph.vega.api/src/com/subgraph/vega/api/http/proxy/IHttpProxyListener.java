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

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

public interface IHttpProxyListener {
	IHttpProxyListenerConfig getConfig();
	IHttpRequestEngine getRequestEngine();
	void start();
	void stop();
	void registerEventHandler(IHttpInterceptProxyEventHandler handler);
	void unregisterEventHandler(IHttpInterceptProxyEventHandler handler);
}
