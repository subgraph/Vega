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
import java.util.Iterator;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

public interface IRequestLog {
	long getNextRequestId();
	long allocateRequestId();
	long addRequestResponse(IHttpResponse response);
	IRequestLogRecord lookupRecord(long requestId);
	List<IRequestLogRecord> getAllRecords();
	List<IRequestLogRecord> getRecordsByConditionSet(IHttpConditionSet filterCondition);
	Iterator<IRequestLogRecord> getRecordIteratorByConditionSet(IHttpConditionSet filterCondition);
	IRequestOriginProxy getRequestOriginProxy(InetAddress address, int port);
	IRequestOriginScanner getRequestOriginScanner(IScanInstance scanInstance);
	IRequestOrigin getRequestOriginRequestEditor();

	void addNewRecordListener(IRequestLogNewRecordListener callback);
	void addNewRecordListener(IRequestLogNewRecordListener callback, IHttpConditionSet filterCondition);
	void removeNewRecordListener(IRequestLogNewRecordListener callback);
}
