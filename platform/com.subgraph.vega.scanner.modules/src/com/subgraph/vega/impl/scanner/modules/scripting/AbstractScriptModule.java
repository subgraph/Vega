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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import com.subgraph.vega.api.scanner.modules.IEnableableModule;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;

public abstract class AbstractScriptModule implements IScannerModule, IEnableableModule {
	private static final Logger logger = Logger.getLogger("script-module");
	protected static class ExportedObject {
		private final String identifier;
		private final Object object;
		ExportedObject(String identifier, Object object) {
			this.identifier = identifier;
			this.object = object;
		}
	}
	
	private final ScriptedModule module;
	private final ScriptedModuleRunningTime runningTime;
	
	private boolean isEnabled;
	
	protected AbstractScriptModule(ScriptedModule module) {
		this.module = module;
		this.isEnabled = module.isDefaultEnabled();
		this.runningTime = new ScriptedModuleRunningTime(module.getModuleName());
	}

	public String getModuleName() {
		return module.getModuleName();
	}
	
	public String getModuleCategoryName() {
		return module.getCategoryName();
	}

	public ModuleScriptType getModuleType() {
		return module.getModuleType();
	}
	
	protected void runScript(List<ExportedObject> exports, String target) {
		try {
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			processExports(exports, instance);
			final long startTS = System.currentTimeMillis();
			module.runModule(cx, instance);
			final long endTS = System.currentTimeMillis();
			runningTime.addTimestamp((int) (endTS - startTS), target);
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script", e).toString());
			e.printStackTrace();
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script.", e).toString());
		} finally {
			Context.exit();
		}
	}
	
	@Override
	public IScannerModuleRunningTime getRunningTimeProfile() {
		return runningTime;
	}
	
	@Override
	public void setEnabled(boolean flag) {
		isEnabled = flag;
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
	
	protected void export(List<ExportedObject> exports, String name, Object object) {
			exports.add(new ExportedObject(name, object));
	}
	
	private void processExports(List<ExportedObject> exports, Scriptable instance) {
		for(ExportedObject exp: exports) {
			Object wrappedObject = Context.javaToJS(exp.object, instance);
			ScriptableObject.putProperty(instance, exp.identifier, wrappedObject);
		}
	}
}
