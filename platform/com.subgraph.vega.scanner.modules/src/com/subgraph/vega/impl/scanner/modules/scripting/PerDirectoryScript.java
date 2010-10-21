package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;

public class PerDirectoryScript extends AbstractScriptModule implements IPerDirectoryScannerModule {
	
	public PerDirectoryScript(ScriptedModule module) {
		super(module);
	}	
	@Override
	public void runScan(IScanDirectory directory,
			IHttpRequestEngine requestEngine, IScanModel scanModel) {
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "directory", directory);
		export(exports, "requestEngine", requestEngine);
		export(exports, "scanModel", scanModel);
		runScript(exports);		
	}

}
