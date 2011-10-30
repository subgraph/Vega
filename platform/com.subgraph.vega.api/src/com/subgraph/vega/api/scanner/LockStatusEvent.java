package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.events.IEvent;

public class LockStatusEvent implements IEvent {
	private final IScan scan;

	public LockStatusEvent(IScan scan) {
		this.scan = scan;
	}
	
	public IScan getScan() {
		return scan;
	}
}
