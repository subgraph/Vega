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

public class ScannerStateSourceProvider extends AbstractSourceProvider {
	public final static String SCAN_SELECTION_STATE = "vega.scanSelectionState"; 
	public final static String SCAN_ACTIVE = "active";
	public final static String SCAN_IDLE = "idle";
	private boolean isScanActive = false;
	
	@Override
	public void dispose() {
	}

	@Override
	synchronized public Map<?,?> getCurrentState() {
		Map<String, String> stateMap = new HashMap<String, String>(1);
		stateMap.put(SCAN_SELECTION_STATE, getCurrentScanSelectionState());
		return stateMap;
	}

	public synchronized void setScanSelectionIsActive(boolean isActive) {
		if (isScanActive != isActive) {
			isScanActive = isActive;
			fireSourceChanged(ISources.WORKBENCH, SCAN_SELECTION_STATE, getCurrentScanSelectionState());
		}
	}

	private String getCurrentScanSelectionState() {
		if (isScanActive) {
			return SCAN_ACTIVE;
		} else {
			return SCAN_IDLE;
		}
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SCAN_SELECTION_STATE, };
	}

}
