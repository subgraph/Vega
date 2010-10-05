package com.subgraph.vega.api.scanner.modules;

import java.util.List;

public interface IScannerModuleRegistry {
	List<IPerHostScannerModule> getPerHostModules();
	List<IPerDirectoryScannerModule> getPerDirectoryModules();
	List<IResponseProcessor> getResponseProcessors();
}
