package com.subgraph.vega.internal.requestlog;

import com.subgraph.vega.api.requestlog.IRequestLogChangeEvent;
import com.subgraph.vega.api.requestlog.IRequestLogRecord;

public class AddRecordEvent implements IRequestLogChangeEvent {

	private final IRequestLogRecord record;
	
	AddRecordEvent(IRequestLogRecord record) {
		this.record = record;
	}
	@Override
	public boolean isRecordAddEvent() {
		return true;
	}

	@Override
	public IRequestLogRecord getRecord() {
		return record;
	}

}
