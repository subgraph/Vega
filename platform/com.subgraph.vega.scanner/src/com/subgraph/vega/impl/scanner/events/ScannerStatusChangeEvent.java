package com.subgraph.vega.impl.scanner.events;

import com.subgraph.vega.api.scanner.IScannerStatusChangeEvent;
import com.subgraph.vega.api.scanner.IScanner.ScannerStatus;

public class ScannerStatusChangeEvent implements IScannerStatusChangeEvent {

	private final ScannerStatus newStatus;
	
	public ScannerStatusChangeEvent(ScannerStatus newStatus) {
		this.newStatus = newStatus;
	}
	
	@Override
	public ScannerStatus getScannerStatus() {
		return newStatus;
	}
	

}
