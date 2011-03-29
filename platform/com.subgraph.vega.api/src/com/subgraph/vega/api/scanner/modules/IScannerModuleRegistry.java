package com.subgraph.vega.api.scanner.modules;

import java.util.List;

public interface IScannerModuleRegistry {
	void refreshModuleScripts();
	void runDomTests();
	List<IPerHostScannerModule> getPerHostModules(boolean enabledOnly);
	List<IPerDirectoryScannerModule> getPerDirectoryModules(boolean enabledOnly);
	List<IPerResourceScannerModule> getPerResourceModules(boolean enabledOnly);
	List<IResponseProcessingModule> getResponseProcessingModules(boolean enabledOnly);
	List<IPerMountPointModule> getPerMountPointModules(boolean enabledOnly);
	List<IBasicModuleScript> getBasicModules(boolean enabledOnly);
	List<IScannerModule> getInternalModules(boolean enabledOnly);
	List<IScannerModule> getAllModules(boolean enabledOnly);
	void resetAllModuleTimestamps();
}
