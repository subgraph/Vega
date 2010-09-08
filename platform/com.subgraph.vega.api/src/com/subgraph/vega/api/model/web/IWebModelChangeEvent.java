package com.subgraph.vega.api.model.web;

import com.subgraph.vega.api.events.IEvent;


public interface IWebModelChangeEvent extends IEvent {
	boolean isEntityAddEvent();
	IWebEntity getEntity();
}
