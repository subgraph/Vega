package com.subgraph.vega.internal.model.requests;

import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLogNewRecordListener;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.RequestLogNewRecordEvent;

public class RequestLogNewRecordListener {
	private final IRequestLogNewRecordListener listenerCallback;
	private final IHttpConditionSet filterCondition;
	
	public RequestLogNewRecordListener(IRequestLogNewRecordListener callback, IHttpConditionSet filter) {
		this.listenerCallback = callback;
		this.filterCondition = filter;
	}
	
	void filterRecord(IRequestLogRecord record) {
		if(matchesRecord(record)) {
			listenerCallback.onNewRecord(new RequestLogNewRecordEvent(record));
		}
	}
	
	IRequestLogNewRecordListener getListener() {
		return listenerCallback;
	}

	private boolean matchesRecord(IRequestLogRecord record) {
		if(filterCondition != null) {
			return filterCondition.matchesAll(record);
		} else {
			return true;
		}
	}
}
