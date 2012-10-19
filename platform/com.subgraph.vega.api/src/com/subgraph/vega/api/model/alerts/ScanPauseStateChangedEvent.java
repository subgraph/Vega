package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.events.IEvent;

public class ScanPauseStateChangedEvent implements IEvent {
	private final boolean isPaused;
	
	public ScanPauseStateChangedEvent(boolean isPaused) {
		this.isPaused = isPaused;
	}
	
	public boolean getPauseState() {
		return isPaused;
	}
}
