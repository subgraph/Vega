package com.subgraph.vega.api.scanner.modules;

import java.util.List;

public interface IScannerModuleRegistry {
	void refreshModuleScripts();
	void runDomTests();
	List<IPerHostScannerModule> getPerHostModules();
	List<IPerDirectoryScannerModule> getPerDirectoryModules();
	List<IResponseProcessingModule> getResponseProcessingModules();
}
