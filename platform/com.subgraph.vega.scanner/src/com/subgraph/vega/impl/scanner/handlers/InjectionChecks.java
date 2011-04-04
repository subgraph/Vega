package com.subgraph.vega.impl.scanner.handlers;

import java.util.List;

import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;

public class InjectionChecks {

	private final PutChecks putChecks;
	private final PageVariabilityCheck pageVariabilityChecks;

	public InjectionChecks() {
		putChecks = new PutChecks(this);
		pageVariabilityChecks = new PageVariabilityCheck(this);
	}

	public void initialize(IPathState ps) {
		ps.unlockChildren();
		if(ps.getPath().getPathType() == PathType.PATH_DIRECTORY)
			putChecks.initialize(ps);
		else
			runPageVariabilityCheck(ps);
	}

	public void runPageVariabilityCheck(IPathState ps) {
		pageVariabilityChecks.initialize(ps);
	}

	public void launchInjectionModules(IPathState ps) {
		List<IBasicModuleScript> crawlerModules = ps.getModuleRegistry().getBasicModules(true);
		for(IBasicModuleScript m: crawlerModules)
			m.runScript(ps);
	}
}
