package com.subgraph.vega.impl.scanner.modules.scripting;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;

import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.scanner.modules.IEnableableModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;

public class BasicModuleScript implements IBasicModuleScript, IEnableableModule {
	private static final Logger logger = Logger.getLogger("script-module");

	private final ScriptedModule module;
	
	public BasicModuleScript(ScriptedModule module) {
		this.module = module;
	}

	@Override
	public void runScript(IPathState pathState) {
		final IModuleContext ctx = pathState.createModuleContext();
		try {
			final Object[] args = new Object[] { new ModuleContextJS(ctx) };
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			module.runModule(cx, instance, args, pathState.toString());
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script", e).toString());
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script.", e).toString());
		} finally {
			Context.exit();
		}
	}

	@Override
	public void setEnabled(boolean flag) {
		module.setEnabledState(flag);		
	}

	@Override
	public boolean isEnabled() {
		return module.getEnabledState();
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
	public IScannerModuleRunningTime getRunningTimeProfile() {
		return module.getRunningTime();
	}
}
