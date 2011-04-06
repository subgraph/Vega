package com.subgraph.vega.api.model.requests;

import com.subgraph.vega.api.events.IEvent;

public class RequestLogUpdateEvent implements IEvent {
	final int count;

	public RequestLogUpdateEvent(int count) {
		this.count = count;
	}

	public int getRecordCount() {
		return count;
	}
}
