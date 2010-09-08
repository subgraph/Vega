package com.subgraph.vega.internal.model.web;

import static com.google.common.base.Preconditions.checkNotNull;

import com.subgraph.vega.api.model.web.IWebEntity;

abstract class AbstractWebEntity implements IWebEntity {
	
	private boolean isVisited = false;
	protected final WebModel model;
	
	AbstractWebEntity(WebModel model) {
		this.model = checkNotNull(model);
	}
	
	public boolean isVisited() {
		return isVisited;
	}
	
	public void setVisited(boolean notify) {
		if(getParent() != null)
			getParent().setVisited(notify);
		if(!isVisited) {
			isVisited = true;
			if(notify)
				model.notifyEntityChanged(this);
		}
	}
	
}
