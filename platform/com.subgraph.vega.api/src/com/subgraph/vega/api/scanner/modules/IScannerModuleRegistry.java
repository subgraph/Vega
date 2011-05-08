package com.subgraph.vega.api.scanner.modules;

import java.util.List;

public interface IScannerModuleRegistry {
	void refreshModuleScripts();
	void runDomTests();
	List<IResponseProcessingModule> getResponseProcessingModules(boolean enabledOnly);
	List<IBasicModuleScript> getBasicModules(boolean enabledOnly);
	List<IScannerModule> getAllModules(boolean enabledOnly);
	void resetAllModuleTimestamps();
}
