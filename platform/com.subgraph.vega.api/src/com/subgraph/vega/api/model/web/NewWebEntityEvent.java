package com.subgraph.vega.api.model.web;

import com.subgraph.vega.api.events.IEvent;

public class NewWebEntityEvent implements IEvent {
	private final IWebEntity entity;
	
	public NewWebEntityEvent(IWebEntity entity) {
		this.entity = entity;
	}
	
	public IWebEntity getEntity() {
		return entity;
	}

}
