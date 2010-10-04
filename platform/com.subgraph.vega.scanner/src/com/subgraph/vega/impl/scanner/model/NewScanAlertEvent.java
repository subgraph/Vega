package com.subgraph.vega.impl.scanner.model;

import com.subgraph.vega.api.scanner.model.INewScanAlertEvent;
import com.subgraph.vega.api.scanner.model.IScanAlert;

public class NewScanAlertEvent implements INewScanAlertEvent {
	private final IScanAlert alert;
	
	NewScanAlertEvent(IScanAlert alert) {
		this.alert = alert;
	}
	
	public IScanAlert getAlert() {
		return alert;
	}

}
