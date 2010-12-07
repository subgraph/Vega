package com.subgraph.vega.api.model;

import com.subgraph.vega.api.events.IEvent;

public class WorkspaceLockStatusEvent implements IEvent {
	private final boolean isLockEvent;
	
	public WorkspaceLockStatusEvent(boolean isLockEvent) {
		this.isLockEvent = isLockEvent;
	}
	
	public boolean isLockEvent() {
		return isLockEvent;
	}
}
