package com.subgraph.vega.api.model.web;

import com.subgraph.vega.api.events.IEvent;

public class UpdatedWebEntityEvent implements IEvent {
	private final IWebEntity entity;
	
	public UpdatedWebEntityEvent(IWebEntity entity) {
		this.entity = entity;
	}
	
	public IWebEntity getEntity() {
		return entity;
	}
}
