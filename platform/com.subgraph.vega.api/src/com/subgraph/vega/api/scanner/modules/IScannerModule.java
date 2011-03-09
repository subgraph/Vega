package com.subgraph.vega.api.scanner.modules;

public interface IScannerModule {
	String getModuleName();
	String getModuleCategoryName();
	IScannerModuleRunningTime getRunningTimeProfile();
}
