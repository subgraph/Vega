package com.subgraph.vega.ui.scanner.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.scanner.IScannerStatusChangeEvent;
import com.subgraph.vega.ui.scanner.Activator;

public class ScannerStateSourceProvider extends AbstractSourceProvider implements IEventHandler {
	final static String SCANNER_STATE = "vega.scannerState";
	final static String SCANNER_RUNNING = "running";
	final static String SCANNER_IDLE = "idle";
	
	private boolean isRunning = false;
	
	public ScannerStateSourceProvider() {
		Activator.getDefault().getScanner().registerScannerStatusChangeListener(this);
	}
	
	@Override
	public void dispose() {		
	}

	@Override
	synchronized public Map<?,?> getCurrentState() {
		System.out.println("Returning current state as "+ getCurrentScannerState());
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
			System.out.println("firing change to "+ getCurrentScannerState());
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
		if(event instanceof IScannerStatusChangeEvent) {
			IScannerStatusChangeEvent statusChange = (IScannerStatusChangeEvent) event;
			switch(statusChange.getScannerStatus()) {
			case SCAN_IDLE:
			case SCAN_COMPLETED:
				setScannerStopped();
				break;
			case SCAN_AUDITING:
			case SCAN_CRAWLING:
			case SCAN_STARTING:
				setScannerRunning();
				break;
			}
		}		
	}

}
