package com.subgraph.vega.api.model.alerts;

import java.util.List;

import com.subgraph.vega.api.events.IEventHandler;

public interface IScanAlertModel {
	void addAlertListenerAndPopulate(IEventHandler listener);
	void removeAlertListener(IEventHandler listener);
	IScanAlert createAlert(String type);
	void addAlert(IScanAlert alert);
	List<IScanAlert> getAlerts();
}
