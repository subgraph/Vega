package com.subgraph.vega.impl.scanner.modules.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class ScriptedModule {
	private final ScriptFile scriptFile;
	
	private final String moduleName;
	private final ModuleScriptType moduleType;
	private final Function runFunction;
	
	
	public ScriptedModule(ScriptFile scriptFile, String moduleName, ModuleScriptType moduleType, Function moduleEntry) {
		this.scriptFile = scriptFile;
		this.moduleName = moduleName;
		this.moduleType = moduleType;
		this.runFunction = moduleEntry;
	}
	
	public Scriptable createInstanceScope(Context cx) {
		Scriptable scope = cx.newObject(scriptFile.getCompiledScript());
		scope.setPrototype(scriptFile.getCompiledScript());
		scope.setParentScope(null);
		return scope;
	}
	
	public void runModule(Context cx, Scriptable instanceScope) {
		runFunction.call(cx, instanceScope, instanceScope, new Object[0]);
	}
	
	public ScriptFile getScriptFile() {
		return scriptFile;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public ModuleScriptType getModuleType() {
		return moduleType;
	}
		
	public Scriptable getModuleScope() {
		return scriptFile.getCompiledScript();
	}
}
