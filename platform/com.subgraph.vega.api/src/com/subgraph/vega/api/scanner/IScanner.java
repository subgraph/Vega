package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.events.IEventHandler;

public interface IScanner {
	enum ScannerStatus { SCAN_IDLE, SCAN_STARTING, SCAN_AUDITING, SCAN_COMPLETED, SCAN_CANCELED };

	ScannerStatus getScannerStatus();
	IScannerConfig createScannerConfig();
	void setScannerConfig(IScannerConfig config);
	IScannerConfig getScannerConfig();
	
	void startScanner(IScannerConfig config);
	void stopScanner();
	void registerScannerStatusChangeListener(IEventHandler listener);
	void runDomTests();
}
