package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IScanner {
	enum ScannerStatus { SCAN_IDLE, SCAN_STARTING, SCAN_CRAWLING, SCAN_AUDITING, SCAN_COMPLETED };
	
	IScanModel getScanModel();
	ScannerStatus getScannerStatus();
	IScannerConfig createScannerConfig();
	void startScanner(IScannerConfig config);
	void stopScanner();
	void registerScannerStatusChangeListener(IEventHandler listener);
	void runDomTests();

}
