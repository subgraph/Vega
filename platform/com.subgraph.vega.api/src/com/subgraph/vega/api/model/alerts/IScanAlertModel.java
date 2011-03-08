package com.subgraph.vega.api.model.alerts;

import java.util.List;

import com.subgraph.vega.api.events.IEventHandler;

public interface IScanAlertModel {
	void addAlertListenerAndPopulate(IEventHandler listener);
	void removeAlertListener(IEventHandler listener);
	IScanAlert createAlert(String type);
	IScanAlert createAlert(String type, String key);
	IScanAlert createAlert(String type, String key, long requestId);
	void addAlert(IScanAlert alert);
	boolean hasAlertKey(String key);
	IScanAlert getAlertByKey(String key);
	List<IScanAlert> getAlerts();
	void lock();
	void unlock();
}
