package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.events.IEvent;

public class RemoveScanInstanceEvent implements IEvent {
	private final IScanInstance scanInstance;

	public RemoveScanInstanceEvent(IScanInstance scanInstance) {
		this.scanInstance = scanInstance;
	}

	public IScanInstance getScanInstance() {
		return scanInstance;
	}
}
