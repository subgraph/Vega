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
package com.subgraph.vega.internal.model.alerts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.db4o.ObjectContainer;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.ScanExceptionEvent;
import com.subgraph.vega.api.model.alerts.ScanPauseStateChangedEvent;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.internal.model.ModelProperties;

public class ScanInstance implements IScanInstance, Activatable {
	private final long scanId;
	private final ModelProperties properties;

	private volatile Date startTime;
	private volatile Date stopTime;
	private volatile int scanStatus;
	
	private transient volatile IScan scan;
	private transient volatile int activeScanCompletedCount;
	private transient volatile int activeScanTotalCount;
	private transient volatile String currentPath;
	private transient volatile boolean isPaused;
	
	private transient ScanAlertRepository repository;
	private transient ScanInstanceAlerts scanAlerts;
	private transient EventListenerManager eventManager;

	private transient Activator activator;

	public ScanInstance(long scanId) {
		this.scanId = scanId;
		this.scanStatus = SCAN_CONFIG;
		this.properties = new ModelProperties();
	}

	public void setTransientState(ObjectContainer database, ScanAlertRepository repository, ScanAlertFactory alertFactory) {
		this.eventManager = new EventListenerManager();
		this.repository = repository;
		this.scanAlerts = new ScanInstanceAlerts(database, this, eventManager, alertFactory);
	}

	@Override
	public long getScanId() {
		activate(ActivationPurpose.READ);
		return scanId;
	}

	@Override
	public IScan getScan() {
		return scan;
	}

	@Override
	public Date getStartTime() {
		activate(ActivationPurpose.READ);
		return startTime;
	}

	@Override
	public Date getStopTime() {
		activate(ActivationPurpose.READ);
		return stopTime;
	}

	@Override
	public IScanAlert createAlert(String type) {
		return createAlert(type, null, -1);
	}

	@Override
	public IScanAlert createAlert(String type, String key) {
		return createAlert(type, key, -1);
	}

	@Override
	public IScanAlert createAlert(String type, String key, long requestId) {
		activate(ActivationPurpose.READ);
		return scanAlerts.createAlert(type, key, requestId);
	}

	@Override
	public void addAlert(IScanAlert alert) {
		activate(ActivationPurpose.READ);
		scanAlerts.addAlert(alert);
	}

	@Override
	public void removeAlert(IScanAlert alert) {
		removeAlerts(Arrays.asList(alert));
	}

	@Override
	public void removeAlerts(Collection<IScanAlert> alerts) {
		activate(ActivationPurpose.READ);
		scanAlerts.removeAlerts(alerts);
		repository.fireRemoveEventsEvent(this, alerts);
	}

	@Override
	public boolean hasAlertKey(String key) {
		return (getAlertByKey(key) != null);
	}

	@Override
	public IScanAlert getAlertByKey(String key) {
		activate(ActivationPurpose.READ);
		return scanAlerts.getAlertByKey(key); 
	}

	@Override
	public List<IScanAlert> getAllAlerts() {
		activate(ActivationPurpose.READ);
		return scanAlerts.getAllAlerts();
	}

	@Override
	public void addScanEventListenerAndPopulate(IEventHandler listener) {
		scanAlerts.addScanEventListenerAndPopulate(listener);
		listener.handleEvent(new ScanStatusChangeEvent(this, currentPath, scanStatus, activeScanCompletedCount, activeScanTotalCount));
	}

	@Override
	public void removeScanEventListener(IEventHandler listener) {
		eventManager.removeListener(listener);
	}

	@Override
	public int getScanStatus() {
		activate(ActivationPurpose.READ);
		return scanStatus;
	}

	@Override
	public boolean isActive() {
		int scanStatus = getScanStatus();
		return (scanStatus == SCAN_PROBING || scanStatus == SCAN_STARTING || scanStatus == SCAN_AUDITING);

	}
	
	@Override
	public boolean isComplete() {
		int scanStatus = getScanStatus();
		return (scanStatus == SCAN_CANCELLED || scanStatus == SCAN_COMPLETED);
	}

	@Override
	public String getScanCurrentPath() {
		return currentPath;
	}

	@Override
	public int getScanCompletedCount() {
		return activeScanCompletedCount;
	}

	@Override
	public int getScanTotalCount() {
		return activeScanTotalCount;
	}

	@Override
	public void setScan(IScan scan) {
		this.scan = scan;
	}

	@Override
	public void updateScanProgress(String currentPath, int completedCount, int totalCount) {
		if(currentPath != null) {
			this.currentPath = currentPath;
		}
		activeScanCompletedCount = completedCount;
		activeScanTotalCount = totalCount;
		eventManager.fireEvent(new ScanStatusChangeEvent(this, this.currentPath, scanStatus, activeScanCompletedCount, activeScanTotalCount));
	}

	@Override
	public void updateScanProgress(int completedCount, int totalCount) {
		activeScanCompletedCount = completedCount;
		activeScanTotalCount = totalCount;
		eventManager.fireEvent(new ScanStatusChangeEvent(this, currentPath, scanStatus, completedCount, totalCount));
	}

	@Override
	public synchronized void updateScanStatus(int status) {
		activate(ActivationPurpose.READ);
		if ((status == SCAN_PROBING || status == SCAN_STARTING) && (startTime == null)) {
			startTime = new Date();
		} else if ( (status == SCAN_COMPLETED || status == SCAN_CANCELLED)  && (stopTime == null) ) {
			stopTime = new Date();
		}
	
		if(status == SCAN_CANCELLED && isPaused) {
			isPaused = false;
		}
	
		this.scanStatus = status;
		eventManager.fireEvent(new ScanStatusChangeEvent(this, currentPath, status, activeScanCompletedCount, activeScanTotalCount));
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void notifyScanException(HttpUriRequest request, Throwable exception) {
		eventManager.fireEvent(new ScanExceptionEvent(request, exception));
	}

	@Override
	public void notifyScanPauseState(boolean isPaused) {
		this.isPaused = isPaused;
		eventManager.fireEvent(new ScanPauseStateChangedEvent(isPaused));
	}
	
	@Override 
	public boolean isScanPaused() {
		return isPaused;
	}

	@Override
	public void deleteScanInstance() {
		eventManager.clearListeners();
		scanAlerts.removeAllAlerts();
	}

	@Override
	public void setProperty(String name, Object value) {
		activate(ActivationPurpose.WRITE);
		properties.setProperty(name, value);
	}

	@Override
	public void setStringProperty(String name, String value) {
		activate(ActivationPurpose.WRITE);
		properties.setStringProperty(name, value);
	}

	@Override
	public void setIntegerProperty(String name, int value) {
		activate(ActivationPurpose.WRITE);
		properties.setIntegerProperty(name, value);
	}

	@Override
	public Object getProperty(String name) {
		activate(ActivationPurpose.READ);
		return properties.getProperty(name);
	}

	@Override
	public String getStringProperty(String name) {
		activate(ActivationPurpose.READ);
		return properties.getStringProperty(name);
	}

	@Override
	public Integer getIntegerProperty(String name) {
		activate(ActivationPurpose.READ);
		return properties.getIntegerProperty(name);
	}

	@Override
	public List<String> propertyKeys() {
		activate(ActivationPurpose.READ);
		return properties.propertyKeys();
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;			
	}
}
