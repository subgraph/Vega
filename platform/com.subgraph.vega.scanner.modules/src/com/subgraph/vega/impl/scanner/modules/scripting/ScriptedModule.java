package com.subgraph.vega.impl.scanner.modules.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;

public class ScriptedModule {
	private final ScriptFile scriptFile;
	
	private final String categoryName;
	private final String moduleName;
	private final ModuleScriptType moduleType;
	private final Function runFunction;
	private final boolean isDisabledInScript;
	private final ScriptedModuleRunningTime runningTime;
	
	private boolean isEnabled;
	
	public ScriptedModule(ScriptFile scriptFile, String categoryName, String moduleName, ModuleScriptType moduleType, Function moduleEntry, boolean isDisabled) {
		this.scriptFile = scriptFile;
		this.categoryName = categoryName;
		this.moduleName = moduleName;
		this.moduleType = moduleType;
		this.runFunction = moduleEntry;
		this.isDisabledInScript = isDisabled;
		this.isEnabled = !isDisabled;
		this.runningTime = new ScriptedModuleRunningTime(moduleName);
	}
	
	public Scriptable createInstanceScope(Context cx) {
		Scriptable scope = cx.newObject(scriptFile.getCompiledScript());
		scope.setPrototype(scriptFile.getCompiledScript());
		scope.setParentScope(null);
		return scope;
	}
	
	public void runModule(Context cx, Scriptable instanceScope, String target) {
		runModule(cx, instanceScope, new Object[0], target);
	}

	public void runModule(Context cx, Scriptable instanceScope, Object[] arguments, String target) {
		final long startTS = System.currentTimeMillis();
		runFunction.call(cx, instanceScope, instanceScope, arguments);
		final long endTS = System.currentTimeMillis();
		runningTime.addTimestamp((int) (endTS - startTS), target);
	}
	
	public ScriptFile getScriptFile() {
		return scriptFile;
	}
	
	public String getCategoryName() {
		return categoryName;
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
	
	public boolean isDisabled() {
		return isDisabledInScript;
	}
	
	public void setEnabledState(boolean flag) {
		isEnabled = flag;
	}
	
	public boolean getEnabledState() {
		return isEnabled;
	}
	public IScannerModuleRunningTime getRunningTime() {
		return runningTime;
	}
}
