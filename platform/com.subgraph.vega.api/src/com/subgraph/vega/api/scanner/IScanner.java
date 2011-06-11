package com.subgraph.vega.api.scanner;

import java.net.URI;
import java.util.List;

import com.subgraph.vega.api.scanner.modules.IScannerModule;


public interface IScanner {
	IScannerConfig createScannerConfig();
	void setScannerConfig(IScannerConfig config);
	IScannerConfig getScannerConfig();
	List<IScannerModule> getAllModules();
	IScanProbeResult probeTargetURI(URI uri);
	void startScanner(IScannerConfig config);
	void stopScanner();
	void runDomTests();
}
