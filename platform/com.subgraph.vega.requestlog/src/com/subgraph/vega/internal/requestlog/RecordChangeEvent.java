package com.subgraph.vega.internal.requestlog;

import com.subgraph.vega.api.requestlog.IRequestLogChangeEvent;
import com.subgraph.vega.api.requestlog.IRequestLogRecord;

public class RecordChangeEvent implements IRequestLogChangeEvent {
	
	private final IRequestLogRecord record;
	
	RecordChangeEvent(IRequestLogRecord record) {
		this.record = record;
	}

	@Override
	public boolean isRecordAddEvent() {
		return false;
	}

	@Override
	public IRequestLogRecord getRecord() {
		return record;
	}
}
