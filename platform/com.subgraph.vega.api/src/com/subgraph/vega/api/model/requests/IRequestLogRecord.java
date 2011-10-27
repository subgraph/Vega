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
package com.subgraph.vega.api.model.requests;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.model.tags.ITaggable;

public interface IRequestLogRecord extends ITaggable {
	long getRequestId();
	long getTimestamp();
	
	/**
	 * Get the end-to-end request execution time in milliseconds.
	 * @return Request execution time in milliseconds, or -1 if unknown.
	 */
	long getRequestMilliseconds();

	/**
	 * Get information about the origin of the request within Vega.
	 * @return Vega request origin.
	 */
	IRequestOrigin getRequestOrigin();
	
	HttpHost getHttpHost();
	HttpRequest getRequest();
	HttpResponse getResponse();
}
