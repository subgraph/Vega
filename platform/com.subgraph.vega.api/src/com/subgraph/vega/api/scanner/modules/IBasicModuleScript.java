package com.subgraph.vega.api.scanner.modules;

import com.subgraph.vega.api.scanner.IPathState;

public interface IBasicModuleScript extends IScannerModule {
	void runScript(IPathState pathState);
}
