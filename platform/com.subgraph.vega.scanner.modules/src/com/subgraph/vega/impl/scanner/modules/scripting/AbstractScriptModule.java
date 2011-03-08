package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;

public abstract class AbstractScriptModule implements IScannerModule {
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
	
	protected AbstractScriptModule(ScriptedModule module) {
		this.module = module;
	}
	
	public String getModuleName() {
		return module.getModuleName();
	}
	
	public ModuleScriptType getModuleType() {
		return module.getModuleType();
	}
	
	protected void runScript(List<ExportedObject> exports, String target) {
		try {
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			processExports(exports, instance);
			module.runModule(cx, instance, target);
			
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script", e).toString());
			e.printStackTrace();
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script.", e).toString());
		} finally {
			Context.exit();
		}
	}
	
	public IScannerModuleRunningTime getRunningTimeProfile() {
		return module.getRunningTime();
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
