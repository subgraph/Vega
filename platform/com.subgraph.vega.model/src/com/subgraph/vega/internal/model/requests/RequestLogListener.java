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
package com.subgraph.vega.internal.model.requests;

import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.IRequestLogUpdateListener;
import com.subgraph.vega.api.model.requests.RequestLogUpdateEvent;

public class RequestLogListener {
	private final IRequestLogUpdateListener listenerCallback;
	private final IHttpConditionSet filterCondition;

	private int count;

	RequestLogListener(IRequestLogUpdateListener callback, IHttpConditionSet filter, int currentCount) {
		this.listenerCallback = callback;
		this.filterCondition = filter;
		this.count = currentCount;
	}

	IRequestLogUpdateListener getListenerCallback() {
		return listenerCallback;
	}

	IHttpConditionSet getFilter() {
		return filterCondition;
	}

	void filterRecord(IRequestLogRecord record) {
		if(matchesRecord(record)) {
			count += 1;
		}
		listenerCallback.update(new RequestLogUpdateEvent(count));
	}

	private boolean matchesRecord(IRequestLogRecord record) {
		if(filterCondition != null)
			return filterCondition.matchesAll(record.getRequest(), record.getResponse());
		else
			return true;
	}
}
