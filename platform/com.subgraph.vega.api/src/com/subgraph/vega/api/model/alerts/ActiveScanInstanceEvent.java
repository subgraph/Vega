package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.events.IEvent;

public class ActiveScanInstanceEvent implements IEvent {
	private final IScanInstance scanInstance;
	
	public ActiveScanInstanceEvent(IScanInstance scanInstance) {
		this.scanInstance = scanInstance;
	}
	
	public IScanInstance getScanInstance() {
		return scanInstance;
	}
}
