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

public interface IRequestLogRecord {
	long getRequestId();
	long getTimestamp();
	long getRequestMilliseconds(); /**< Request execution time in milliseconds. Returns -1 when unknown. */
	HttpHost getHttpHost();
	HttpRequest getRequest();
	HttpResponse getResponse();
}
