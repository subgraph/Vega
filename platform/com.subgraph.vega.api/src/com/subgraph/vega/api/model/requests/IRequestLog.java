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

import java.net.InetAddress;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

public interface IRequestLog {
	long allocateRequestId();
	long addRequestResponse(IHttpResponse response);
	IRequestLogRecord lookupRecord(long requestId);
	List<IRequestLogRecord> getAllRecords();
	List<IRequestLogRecord> getRecordsByConditionSet(IHttpConditionSet filterCondition);
	
	IRequestOriginProxy getRequestOriginProxy(InetAddress address, int port);
	IRequestOriginScanner getRequestOriginScanner(long scanId);
	IRequestOrigin getRequestOriginRequestEditor();

	void addUpdateListener(IRequestLogUpdateListener callback);
	void addUpdateListener(IRequestLogUpdateListener callback, IHttpConditionSet filterCondition);
	void removeUpdateListener(IRequestLogUpdateListener callback);
}
