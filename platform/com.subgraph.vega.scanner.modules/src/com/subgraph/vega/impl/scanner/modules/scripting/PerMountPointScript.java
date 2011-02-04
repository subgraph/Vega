package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebMountPoint;
import com.subgraph.vega.api.scanner.modules.IPerMountPointModule;

public class PerMountPointScript extends AbstractScriptModule implements IPerMountPointModule {
	
	public PerMountPointScript(ScriptedModule module) {
		super(module);
	}

	@Override
	public void runModule(IWebMountPoint mountPoint,
			IHttpRequestEngine requestEngine, IWorkspace workspace) {
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "mountpoint", mountPoint);
		export(exports, "requestEngine", requestEngine);
		export(exports, "workspace", workspace);
		runScript(exports);		
	}

}
