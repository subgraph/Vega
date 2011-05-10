package com.subgraph.vega.api.model.alerts;

import java.util.List;

import com.subgraph.vega.api.events.IEventHandler;

public interface IScanAlertRepository {
	final static int PROXY_ALERT_ORIGIN_SCAN_ID = -1;
	void addAlertListenerAndPopulate(IEventHandler listener);
	void removeAlertListener(IEventHandler listener);
	List<IScanInstance> getAllScanInstances();
	IScanInstance createNewScanInstance();
	IScanInstance getScanInstanceByScanId(long scanId);
	IScanInstance getProxyScanInstance();
}
