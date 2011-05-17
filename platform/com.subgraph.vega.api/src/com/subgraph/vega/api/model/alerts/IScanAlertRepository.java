package com.subgraph.vega.api.model.alerts;

import java.util.List;

import com.subgraph.vega.api.events.IEventHandler;

public interface IScanAlertRepository {
	final static int PROXY_ALERT_ORIGIN_SCAN_ID = -1;
	IScanInstance addActiveScanInstanceListener(IEventHandler listener);
	void removeActiveScanInstanceListener(IEventHandler listener);
	void setActiveScanInstance(IScanInstance scanInstance);
	IScanInstance getActiveScanInstance();
	List<IScanInstance> getAllScanInstances();
	IScanInstance createNewScanInstance();
	IScanInstance getScanInstanceByScanId(long scanId);
	IScanInstance getProxyScanInstance();
}
