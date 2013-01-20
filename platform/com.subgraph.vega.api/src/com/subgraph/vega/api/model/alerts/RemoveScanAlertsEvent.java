package com.subgraph.vega.api.model.alerts;

import java.util.Collection;

import com.subgraph.vega.api.events.IEvent;

public class RemoveScanAlertsEvent implements IEvent {
	private final IScanInstance scanInstance;
	private final Collection<IScanAlert> removedAlerts;
	
	public RemoveScanAlertsEvent(IScanInstance scanInstance, Collection<IScanAlert> removedAlerts) {
		this.scanInstance = scanInstance;
		this.removedAlerts = removedAlerts;
	}

	public IScanInstance getScanInstance() {
		return scanInstance;
	}

	public Collection<IScanAlert> getRemovedEvents() {
		return removedAlerts;
	}
}
