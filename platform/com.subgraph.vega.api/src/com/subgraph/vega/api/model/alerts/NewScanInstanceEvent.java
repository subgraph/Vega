package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.events.IEvent;

public class NewScanInstanceEvent implements IEvent {
	private final IScanInstance scanInstance;
	
	public NewScanInstanceEvent(IScanInstance scanInstance) {
		this.scanInstance = scanInstance;
	}
	
	public IScanInstance getScanInstance() {
		return scanInstance;
	}
}
