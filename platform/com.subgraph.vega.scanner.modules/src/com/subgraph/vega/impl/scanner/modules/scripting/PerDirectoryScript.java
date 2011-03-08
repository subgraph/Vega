package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;

public class PerDirectoryScript extends AbstractScriptModule implements IPerDirectoryScannerModule {
	
	public PerDirectoryScript(ScriptedModule module) {
		super(module);
	}	
	@Override
	public void runScan(IWebPath directory,
			IHttpRequestEngine requestEngine, IWorkspace workspace) {
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "directory", directory);
		export(exports, "requestEngine", requestEngine);
		export(exports, "workspace", workspace);
		runScript(exports, directory.getUri().toString());		
	}

}
