/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.api.model.alerts;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModelProperties;
import com.subgraph.vega.api.scanner.IScan;

public interface IScanInstance extends IModelProperties {
	final static int SCAN_CONFIG = 0; /** Scan is being configured */
	final static int SCAN_PROBING = 1; /** Pre-scan site probing */
	final static int SCAN_STARTING = 2; /** Scan is starting */
	final static int SCAN_AUDITING = 3;
	final static int SCAN_COMPLETED = 4;
	final static int SCAN_CANCELLED = 5;
	
	IScanAlert createAlert(String type);
	IScanAlert createAlert(String type, String key);
	IScanAlert createAlert(String type, String key, long requestId);
	void addAlert(IScanAlert alert);
	void removeAlert(IScanAlert alert);
	void removeAlerts(Collection<IScanAlert> alerts);
	boolean hasAlertKey(String key);
	IScanAlert getAlertByKey(String key);
	List<IScanAlert> getAllAlerts();
	
	long getScanId();
	IScan getScan();
	
	/**
	 * Get the scan start time. Returns null until state is SCAN_PROBING or greater.
	 * @return Scan start time, or null.
	 */
	Date getStartTime();

	/**
	 * Get the scan stop time. Returns null until state is SCAN_COMPLETED or SCAN_CANCELLED.
	 * @return Scan start time, or null.
	 */
	Date getStopTime();
	
	int getScanStatus();
	boolean isActive();
	boolean isComplete();
	int getScanCompletedCount();
	int getScanTotalCount();
	String getScanCurrentPath();
	void setScan(IScan scan);
	void updateScanProgress(String currentPath, int completedCount, int totalCount);
	void updateScanProgress(int completedCount, int totalCount);
	void updateScanStatus(int status);
	void notifyScanException(HttpUriRequest request, Throwable exception);
	void notifyScanPauseState(boolean isPaused);
	boolean isScanPaused();
	
	void addScanEventListenerAndPopulate(IEventHandler listener);
	void removeScanEventListener(IEventHandler listener);
	
	void deleteScanInstance();
}
