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

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

/**
 * Optionally modifies a HttpContext or HttpRequest before a request is sent by the request engine.
 */
public interface IHttpRequestModifier {
	/**
	 * Process a request before it is sent.
	 * @param request HttpRequest.
	 * @param context HttpContext.
	 */
	void process(HttpRequest request, HttpContext context);	
}
