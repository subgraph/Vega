package com.subgraph.vega.impl.scanner.handlers;

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
		if(ps.doInjectionChecks())
			pageVariabilityChecks.initialize(ps);
		else
			ps.setDone();
	}

	public void launchInjectionModules(IPathState ps) {
		for(IBasicModuleScript m: ps.getInjectionModules()) {
			if(m.isEnabled()) {
				m.runScript(ps);
			}
		}
	}
}
