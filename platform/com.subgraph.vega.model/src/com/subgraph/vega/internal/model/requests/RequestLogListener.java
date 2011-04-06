package com.subgraph.vega.internal.model.requests;

import com.subgraph.vega.api.model.requests.IRequestLogFilter;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.IRequestLogUpdateListener;
import com.subgraph.vega.api.model.requests.RequestLogUpdateEvent;

public class RequestLogListener {
	private final IRequestLogUpdateListener listenerCallback;
	private final IRequestLogFilter filter;

	private int count;

	RequestLogListener(IRequestLogUpdateListener callback, IRequestLogFilter filter, int currentCount) {
		this.listenerCallback = callback;
		this.filter = filter;
		this.count = currentCount;
	}

	IRequestLogUpdateListener getListenerCallback() {
		return listenerCallback;
	}

	IRequestLogFilter getFilter() {
		return filter;
	}

	void filterRecord(IRequestLogRecord record) {
		if(matchesRecord(record))
			count += 1;
		listenerCallback.update(new RequestLogUpdateEvent(count));
	}

	private boolean matchesRecord(IRequestLogRecord record) {
		if(filter != null)
			return filter.match(record);
		else
			return true;
	}

}
