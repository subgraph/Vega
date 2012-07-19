package com.subgraph.vega.api.model.scope;

import com.subgraph.vega.api.events.IEvent;

public class ActiveScopeChangedEvent implements IEvent {
	private final ITargetScope activeScope;
	
	public ActiveScopeChangedEvent(ITargetScope activeScope) {
		this.activeScope = activeScope;
	}
	
	public ITargetScope getActiveScope() {
		return activeScope;
	}

}
