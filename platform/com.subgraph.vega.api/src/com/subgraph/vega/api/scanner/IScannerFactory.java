package com.subgraph.vega.api.scanner;

public interface IScannerFactory {
	IScannerConfig createScannerConfig();
	IScanner createScanner(IScannerConfig config);

}
