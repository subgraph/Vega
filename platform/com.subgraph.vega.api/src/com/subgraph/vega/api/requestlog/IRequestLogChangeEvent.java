package com.subgraph.vega.api.requestlog;

import com.subgraph.vega.api.events.IEvent;

public interface IRequestLogChangeEvent extends IEvent {
	boolean isRecordAddEvent();
	IRequestLogRecord getRecord();
}
