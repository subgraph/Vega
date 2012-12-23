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
package com.subgraph.vega.impl.scanner.modules.scripting;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;

import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.scanner.modules.IEnableableModule;

public class BasicModuleScript implements IBasicModuleScript, IEnableableModule {
	private static final Logger logger = Logger.getLogger("script-module");

	private final ScriptedModule module;
	private final ScriptedModuleRunningTime runningTime;
	
	private boolean isEnabled;
	
	public BasicModuleScript(ScriptedModule module, boolean isEnabled, ScriptedModuleRunningTime runningTime) {
		this.module = module;
		this.isEnabled = isEnabled;
		this.runningTime = runningTime;
	}
	public BasicModuleScript(ScriptedModule module) {
		this.module = module;
		this.isEnabled = module.isDefaultEnabled();
		this.runningTime = new ScriptedModuleRunningTime(module.getModuleName());
	}

	public ScriptedModule getModule() {
		return module;
	}

	@Override
	public void runScript(IPathState pathState) {
		final IInjectionModuleContext ctx = pathState.createModuleContext();
		try {
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			final Object[] args = new Object[] { new ModuleContextJS(instance, ctx) };
			final long startTS = System.currentTimeMillis();
			module.runModule(cx, instance, args);
			final long endTS = System.currentTimeMillis();
			runningTime.addTimestamp((int) (endTS - startTS), pathState.toString());
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script: "+ module.getModuleName(), e).toString());
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script: "+ module.getModuleName(), e).toString());
		} finally {
			Context.exit();
		}
	}

	@Override
	public void setEnabled(boolean flag) {
		isEnabled = flag;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public String getModuleName() {
		return module.getModuleName();
	}

	@Override
	public String getModuleCategoryName() {
		return module.getCategoryName();
	}

	@Override
	public ScriptedModuleRunningTime getRunningTimeProfile() {
		return runningTime;
	}
	@Override
	public boolean isDifferential() {
		return module.isDifferential();
	}
	@Override
	public boolean isTimeSensitive() {
		return module.isTimeSensitive();
	}
}
