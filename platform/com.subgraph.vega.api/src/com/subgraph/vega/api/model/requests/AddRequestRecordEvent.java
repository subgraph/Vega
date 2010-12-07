package com.subgraph.vega.api.model.requests;

import com.subgraph.vega.api.events.IEvent;

public class AddRequestRecordEvent implements IEvent {
	private final IRequestLogRecord record;
	
	public AddRequestRecordEvent(IRequestLogRecord record) {
		this.record = record;
	}

	public IRequestLogRecord getRecord() {
		return record;
	}
}
