package com.subgraph.vega.ui.scanner.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;

public class PauseStateSourceProvider extends AbstractSourceProvider {
	public final static String PAUSE_STATE = "vega.pauseState";
	
	private final static String PAUSE_PAUSEABLE = "pauseable";
	private final static String PAUSE_PAUSED = "paused";
	private final static String PAUSE_NOTPAUSEABLE = "not_pauseable";
	
	boolean isPauseable = false;
	boolean isPaused = false;
	
	@Override
	public void dispose() {
	}

	@Override
	public Map<?,?> getCurrentState() {
		final Map<String,String> stateMap = new HashMap<String,String>(1);
		stateMap.put(PAUSE_STATE, getCurrentPauseState());
		return stateMap;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { PAUSE_STATE };
	}
	
	private String getCurrentPauseState() {
		if(!isPauseable) {
			return PAUSE_NOTPAUSEABLE;
		} else if(isPaused) {
			return PAUSE_PAUSED;
		} else {
			return PAUSE_PAUSEABLE;
		}
	}

	public void setSelectedScan(IScanInstance scanInstance) {
		if(scanInstance == null || scanInstance.getScan() == null 
				|| scanInstance.getScanStatus() != IScanInstance.SCAN_AUDITING
				|| scanInstance.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
			isPauseable = false;
			isPaused = false;
			fireSourceChanged(ISources.WORKBENCH, PAUSE_STATE, getCurrentState());
			return;
		}
		isPauseable = true;
		isPaused = scanInstance.getScan().isPausedScan();
		fireSourceChanged(ISources.WORKBENCH, PAUSE_STATE, getCurrentPauseState());
	}
}
