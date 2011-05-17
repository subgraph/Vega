package com.subgraph.vega.api.scanner;


public interface IScanner {
	IScannerConfig createScannerConfig();
	void setScannerConfig(IScannerConfig config);
	IScannerConfig getScannerConfig();
	
	void startScanner(IScannerConfig config);
	void stopScanner();
	void runDomTests();
}
