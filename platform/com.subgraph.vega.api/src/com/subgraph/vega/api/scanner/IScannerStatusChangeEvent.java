package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.scanner.IScanner.ScannerStatus;

public interface IScannerStatusChangeEvent extends IEvent {
	ScannerStatus getScannerStatus();
}
