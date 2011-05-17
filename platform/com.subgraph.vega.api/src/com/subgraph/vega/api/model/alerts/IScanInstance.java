package com.subgraph.vega.api.model.alerts;

import java.util.Date;
import java.util.List;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModelProperties;

public interface IScanInstance extends IModelProperties {
	final static int SCAN_IDLE = 1;
	final static int SCAN_STARTING = 2;
	final static int SCAN_AUDITING = 3;
	final static int SCAN_COMPLETED = 4;
	final static int SCAN_CANCELLED = 5;
	
	IScanAlert createAlert(String type);
	IScanAlert createAlert(String type, String key);
	IScanAlert createAlert(String type, String key, long requestId);
	void addAlert(IScanAlert alert);
	boolean hasAlertKey(String key);
	IScanAlert getAlertByKey(String key);
	List<IScanAlert> getAllAlerts();
	
	long getScanId();
	Date getStartTime();
	int getScanStatus();
	int getScanCompletedCount();
	int getScanTotalCount();
	void updateScanProgress(int completedCount, int totalCount);
	void updateScanStatus(int status);
	
	void addScanEventListenerAndPopulate(IEventHandler listener);
	void removeScanEventListener(IEventHandler listener);
	void lock();
	void unlock();
}
