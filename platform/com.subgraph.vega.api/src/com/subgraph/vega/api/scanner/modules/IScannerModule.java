package com.subgraph.vega.api.scanner.modules;

public interface IScannerModule {

	String getModuleName();
	ModuleScriptType getModuleType();
	IScannerModuleRunningTime getRunningTimeProfile();
}
