package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IScannerFactory {
	IScanModel getScanModel();
	IScannerConfig createScannerConfig();
	IScanner createScanner(IScannerConfig config);
	void runDomTests();
}
