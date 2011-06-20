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
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.LockStatusEvent;
import com.subgraph.vega.ui.scanner.Activator;

public class ScannerStateSourceProvider extends AbstractSourceProvider implements IEventHandler {
	final static String SCANNER_STATE = "vega.scannerState";
	final static String SCANNER_RUNNING = "running";
	final static String SCANNER_IDLE = "idle";
	
	private boolean isRunning = false;

	public ScannerStateSourceProvider() {
		final IScanner scanner = Activator.getDefault().getScanner();
		scanner.addLockStatusListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getScanner().removeLockStatusListener(this);
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
		if(event instanceof LockStatusEvent) {
			handleLockStatus((LockStatusEvent) event);
		}
	}

	private void handleLockStatus(LockStatusEvent event) {
		if(event.isLocked()) {
			setScannerRunning();
		} else {
			setScannerStopped();
		}
	}
}
