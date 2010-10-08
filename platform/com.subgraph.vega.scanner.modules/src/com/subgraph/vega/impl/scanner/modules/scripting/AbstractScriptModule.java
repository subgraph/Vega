package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public abstract class AbstractScriptModule {
	private static final Logger logger = Logger.getLogger("script-module");
	private static class ExportedObject {
		private final String identifier;
		private final Object object;
		ExportedObject(String identifier, Object object) {
			this.identifier = identifier;
			this.object = object;
		}
	}
	
	private final ScriptedModule module;
	private final List<ExportedObject> currentExports = new ArrayList<ExportedObject>();
	
	protected AbstractScriptModule(ScriptedModule module) {
		this.module = module;
	}
	
	protected void runScript() {
		try {
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			processExports(instance);
			module.runModule(cx, instance);
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script.", e).toString());
		} finally {
			Context.exit();
		}
	}
	
	protected void export(String name, Object object) {
		currentExports.add(new ExportedObject(name, object));
	}
	
	private void processExports(Scriptable instance) {
		for(ExportedObject exp: currentExports) {
			Object wrappedObject = Context.javaToJS(exp.object, instance);
			ScriptableObject.putProperty(instance, exp.identifier, wrappedObject);
		}
		currentExports.clear();
	}

}
