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
package com.subgraph.vega.ui.scanner.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.ActiveScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.ui.scanner.Activator;

public class ScannerStateSourceProvider extends AbstractSourceProvider implements IEventHandler {
	final static String SCANNER_STATE = "vega.scannerState";
	final static String SCANNER_RUNNING = "running";
	final static String SCANNER_IDLE = "idle";
	
	private boolean isRunning = false;
	private IWorkspace currentWorkspace;
	private IScanInstance activeScanInstance;
	public ScannerStateSourceProvider() {
		currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(this);
		if(currentWorkspace != null) {
			activeScanInstance = currentWorkspace.getScanAlertRepository().addActiveScanInstanceListener(this);
			if(activeScanInstance != null) {
				activeScanInstance.addScanEventListenerAndPopulate(this);
			}
		}
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getModel().removeWorkspaceListener(this);
	}

	@Override
	synchronized public Map<?,?> getCurrentState() {
		Map<String, String> stateMap = new HashMap<String, String>(1);
		stateMap.put(SCANNER_STATE, getCurrentScannerState());
		return stateMap;
	}

	synchronized void setScannerRunning() {
		setScannerState(true);
	}
	
	synchronized void setScannerStopped() {
		setScannerState(false);
	}
	
	private void setScannerState(boolean state) {
		if(state != isRunning) {
			isRunning = state;
			fireSourceChanged(ISources.WORKBENCH, SCANNER_STATE, getCurrentScannerState());
		}
	}
	
	private String getCurrentScannerState() {
		if(isRunning)
			return SCANNER_RUNNING;
		else
			return SCANNER_IDLE;
	}
	
	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SCANNER_STATE };
	}
	
	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof WorkspaceOpenEvent) {
			handleWorkspaceOpen((WorkspaceOpenEvent) event);
		} else if(event instanceof WorkspaceCloseEvent) {
			handleWorkspaceClose((WorkspaceCloseEvent) event);
		} else if(event instanceof WorkspaceResetEvent) {
			handleWorkspaceReset((WorkspaceResetEvent) event);
		} else if(event instanceof ActiveScanInstanceEvent) {
			handleActiveScanInstance((ActiveScanInstanceEvent) event);
		} else if(event instanceof ScanStatusChangeEvent) {
			handleScanStatusChange((ScanStatusChangeEvent) event);
		}	
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		currentWorkspace = event.getWorkspace();
		setActiveScanInstance(currentWorkspace.getScanAlertRepository().addActiveScanInstanceListener(this));
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		setActiveScanInstance(null);
		if(currentWorkspace != null) {
			currentWorkspace.getScanAlertRepository().removeActiveScanInstanceListener(this);
			currentWorkspace = null;
		}
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		if(currentWorkspace != null) {
			currentWorkspace.getScanAlertRepository().removeActiveScanInstanceListener(this);
			currentWorkspace = null;
		}
		currentWorkspace = event.getWorkspace();
		setActiveScanInstance(currentWorkspace.getScanAlertRepository().addActiveScanInstanceListener(this));
	}
	
	private void handleActiveScanInstance(ActiveScanInstanceEvent event) {
		setActiveScanInstance(event.getScanInstance());
	}
	
	private void handleScanStatusChange(ScanStatusChangeEvent event) {
		scanStateToEnableState(event.getStatus());
	}
	
	private void setActiveScanInstance(IScanInstance scan) {
		if(activeScanInstance != null) {
			activeScanInstance.removeScanEventListener(this);
		}

		if(scan != null) {
			activeScanInstance = scan;
			activeScanInstance.addScanEventListenerAndPopulate(this);
			scanStateToEnableState(activeScanInstance.getScanStatus());
		} else {
			activeScanInstance = null;
			setScannerStopped();
		}
	}

	private void scanStateToEnableState(int scanState) {
		switch(scanState) {
		case IScanInstance.SCAN_AUDITING:
		case IScanInstance.SCAN_STARTING:
			setScannerRunning();
			break;
		case IScanInstance.SCAN_IDLE:
		case IScanInstance.SCAN_CANCELLED:
		case IScanInstance.SCAN_COMPLETED:
			setScannerStopped();
			break;
		}
	}

}
