package com.subgraph.vega.api.scanner.modules;

import java.util.List;

import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IScannerModuleRegistry {
	void setScanModel(IScanModel scanModel);
	void refreshModuleScripts();
	void runDomTests();
	List<IPerHostScannerModule> getPerHostModules();
	List<IPerDirectoryScannerModule> getPerDirectoryModules();
	List<IResponseProcessingModule> getResponseProcessingModules();
}
