package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.modules.IPerResourceScannerModule;

public class PerResourceScript extends AbstractScriptModule implements IPerResourceScannerModule {

	public PerResourceScript(ScriptedModule module) {
		super(module);
	}
	
	@Override
	public void runModule(IWebPath path, IHttpRequestEngine requestEngine,
			IWorkspace workspace) {
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "path", path);
		export(exports, "requestEngine", requestEngine);
		export(exports, "workspace", workspace);
		runScript(exports, path.getUri().toString());
	}

}
