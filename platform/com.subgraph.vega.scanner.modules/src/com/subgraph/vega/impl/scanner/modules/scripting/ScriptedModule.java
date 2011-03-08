package com.subgraph.vega.impl.scanner.modules.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;

public class ScriptedModule {
	private final ScriptFile scriptFile;
	
	private final String moduleName;
	private final ModuleScriptType moduleType;
	private final Function runFunction;
	private final ScriptedModuleRunningTime runningTime;
	
	
	public ScriptedModule(ScriptFile scriptFile, String moduleName, ModuleScriptType moduleType, Function moduleEntry) {
		this.scriptFile = scriptFile;
		this.moduleName = moduleName;
		this.moduleType = moduleType;
		this.runFunction = moduleEntry;
		this.runningTime = new ScriptedModuleRunningTime(moduleName);
	}
	
	public Scriptable createInstanceScope(Context cx) {
		Scriptable scope = cx.newObject(scriptFile.getCompiledScript());
		scope.setPrototype(scriptFile.getCompiledScript());
		scope.setParentScope(null);
		return scope;
	}
	
	public void runModule(Context cx, Scriptable instanceScope, String target) {
		final long startTS = System.currentTimeMillis();
		runFunction.call(cx, instanceScope, instanceScope, new Object[0]);
		final long endTS = System.currentTimeMillis();
		runningTime.addTimestamp((int) (endTS - startTS), target);
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
	
	public IScannerModuleRunningTime getRunningTime() {
		return runningTime;
	}
}
