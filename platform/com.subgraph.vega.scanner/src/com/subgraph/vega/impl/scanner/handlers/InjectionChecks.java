/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.impl.scanner.handlers;

import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectionChecks {

	private final PutChecks putChecks;
	private final PageVariabilityCheck pageVariabilityChecks;

	public InjectionChecks() {
		putChecks = new PutChecks(this);
		pageVariabilityChecks = new PageVariabilityCheck(this);
	}

	public void initialize(IPathState ps) {
		ps.unlockChildren();
		if(ps.getPath().getPathType() == PathType.PATH_DIRECTORY) {
			putChecks.initialize(ps);
		} if(ps.isParametric()) {
			launchInjectionModules(ps);
		} else {
			runPageVariabilityCheck(ps);
		}
	}

	public void runPageVariabilityCheck(IPathState ps) {
		if(ps.doInjectionChecks()) {
			pageVariabilityChecks.initialize(ps);
		} else {
			ps.setDone(); 
		}
	}

	public void launchInjectionModules(IPathState ps) {
		for(IBasicModuleScript m: ps.getInjectionModules()) {
			if(m.isEnabled() && !(ps.getResponseVaries() && m.isDifferential())) {
				m.runScript(ps);
			}
		}
		((PathState)ps).setFinishOnNoRequests();
	}
}
