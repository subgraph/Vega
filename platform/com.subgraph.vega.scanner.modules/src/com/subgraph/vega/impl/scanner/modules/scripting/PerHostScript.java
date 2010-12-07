package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;

public class PerHostScript extends AbstractScriptModule implements IPerHostScannerModule {
	
	public PerHostScript(ScriptedModule module) {
		super(module);
	}
	
	@Override
	public void runScan(IWebHost host, IHttpRequestEngine requestEngine, IWorkspace workspace) {
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "host", host);
		export(exports, "requestEngine", requestEngine);
		export(exports, "workspace", workspace);
		runScript(exports);
	}
}
