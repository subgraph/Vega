package com.subgraph.vega.api.model.alerts;

import java.util.List;

import com.subgraph.vega.api.model.IModelProperties;

public interface IScanInstance extends IModelProperties, IScanIdProvider {
	IScanAlert createAlert(String type);
	IScanAlert createAlert(String type, String key);
	IScanAlert createAlert(String type, String key, long requestId);
	void addAlert(IScanAlert alert);
	boolean hasAlertKey(String key);
	IScanAlert getAlertByKey(String key);
	List<IScanAlert> getAllAlerts();
	void lock();
	void unlock();
}
