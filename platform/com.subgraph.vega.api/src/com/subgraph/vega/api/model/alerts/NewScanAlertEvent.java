package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.events.IEvent;

public class NewScanAlertEvent implements IEvent {
	private final IScanAlert alert;
	
	public NewScanAlertEvent(IScanAlert alert) {
		this.alert = alert;
	}
	
	public IScanAlert getAlert() {
		return alert;
	}

}
