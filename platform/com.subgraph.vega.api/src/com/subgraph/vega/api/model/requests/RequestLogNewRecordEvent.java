package com.subgraph.vega.api.model.requests;

import com.subgraph.vega.api.events.IEvent;

public class RequestLogNewRecordEvent implements IEvent {
	
	private final IRequestLogRecord record;
	
	public RequestLogNewRecordEvent(IRequestLogRecord record) {
		this.record = record;
	}
	
	public IRequestLogRecord getNewRecord() {
		return record;
	}
}
