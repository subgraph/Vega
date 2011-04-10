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
		if(matchesRecord(record))
			count += 1;
		listenerCallback.update(new RequestLogUpdateEvent(count));
	}

	private boolean matchesRecord(IRequestLogRecord record) {
		if(filterCondition != null)
			return filterCondition.matches(record.getRequest(), record.getResponse());
		else
			return true;
	}
}
