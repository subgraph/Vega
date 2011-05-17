package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.events.IEvent;

public class ScanStatusChangeEvent implements IEvent {
	final int scanStatus;
	final int scanCompletedCount;
	final int scanTotalCount;
	
	public ScanStatusChangeEvent(int status, int completed, int total) {
		this.scanStatus = status;
		this.scanCompletedCount = completed;
		this.scanTotalCount = total;
	}
	
	public int getStatus() {
		return scanStatus;
	}
	
	public int getCompletedCount() {
		return scanCompletedCount;
	}
	
	public int getTotalCount() {
		return scanTotalCount;
	}
}
