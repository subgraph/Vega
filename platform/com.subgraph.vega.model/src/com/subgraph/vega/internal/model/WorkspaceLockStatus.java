package com.subgraph.vega.internal.model;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.WorkspaceLockStatusEvent;

public class WorkspaceLockStatus {
	private final EventListenerManager eventManager;
	private int lockCount = 0;
	
	WorkspaceLockStatus(EventListenerManager eventManager) {
		this.eventManager = eventManager;
	}
	
	synchronized void lock() {
		if(lockCount == 0)
			eventManager.fireEvent(new WorkspaceLockStatusEvent(true));
		lockCount += 1;
	}
	
	synchronized void unlock() {
		if(lockCount == 0)
			throw new IllegalStateException("Cannot call unlock() on unlocked workspace.");
		if(lockCount == 1)
			eventManager.fireEvent(new WorkspaceLockStatusEvent(false));
		lockCount -= 1;
	}
	
	synchronized boolean isLocked() {
		return lockCount != 0;
	}
}
