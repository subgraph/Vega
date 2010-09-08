package com.subgraph.vega.internal.model.web;

import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebModelChangeEvent;

public class EntityChangeEvent implements IWebModelChangeEvent {

	private final IWebEntity entity;
	
	EntityChangeEvent(IWebEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public boolean isEntityAddEvent() {
		return false;
	}

	@Override
	public IWebEntity getEntity() {
		return entity;
	}

}
