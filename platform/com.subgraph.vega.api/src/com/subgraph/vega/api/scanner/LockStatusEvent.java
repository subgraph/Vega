package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.events.IEvent;

public class LockStatusEvent implements IEvent {

	private final boolean isLocked;

	public LockStatusEvent(boolean isLocked) {
		this.isLocked = isLocked;
	}
	
	public boolean isLocked() {
		return isLocked;
	}
}
