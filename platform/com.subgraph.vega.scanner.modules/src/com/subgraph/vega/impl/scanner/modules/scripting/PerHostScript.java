package com.subgraph.vega.impl.scanner.modules.scripting;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.model.IScanHost;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;

public class PerHostScript extends AbstractScriptModule implements IPerHostScannerModule {
	
	public PerHostScript(ScriptedModule module) {
		super(module);
	}
	
	@Override
	public void runScan(IScanHost host, IHttpRequestEngine requestEngine, IScanModel scanModel) {
		export("host", host);
		export("requestEngine", requestEngine);
		export("scanModel", scanModel);
		runScript();
	}
	
}
