package com.subgraph.vega.api.scanner.modules;

import java.util.List;

public interface IScannerModuleRegistry {
	final static int PROXY_SCAN_ID = -1;
	void runDomTests();
	List<IResponseProcessingModule> getResponseProcessingModules();
	List<IResponseProcessingModule> updateResponseProcessingModules(List<IResponseProcessingModule> currentModules);
	
	List<IBasicModuleScript> getBasicModules();
	List<IBasicModuleScript> updateBasicModules(List<IBasicModuleScript> currentModules);
	
}
