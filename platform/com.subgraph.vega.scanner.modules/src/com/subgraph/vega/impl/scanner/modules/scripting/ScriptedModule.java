package com.subgraph.vega.impl.scanner.modules.scripting;

import java.io.File;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class ScriptedModule {
	private final File scriptFile;
	private final Scriptable moduleScope;
	private final String moduleName;
	private final ModuleScriptType moduleType;
	private final Function runFunction;
	
	private long fileLastModified;
	
	
	public ScriptedModule(File scriptFile, Scriptable moduleScope, String moduleName, ModuleScriptType moduleType, Function moduleEntry) {
		this.scriptFile = scriptFile;
		this.moduleScope = moduleScope;
		this.moduleName = moduleName;
		this.moduleType = moduleType;
		this.runFunction = moduleEntry;
		this.fileLastModified = scriptFile.lastModified();
	}
	
	
	public Scriptable createInstanceScope(Context cx) {
		Scriptable scope = cx.newObject(moduleScope);
		scope.setPrototype(moduleScope);
		scope.setParentScope(null);
		return scope;
	}
	
	public void runModule(Context cx, Scriptable instanceScope) {
		runFunction.call(cx, instanceScope, instanceScope, new Object[0]);
	}
	
	public File getScriptFile() {
		return scriptFile;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public ModuleScriptType getModuleType() {
		return moduleType;
	}
	
	public boolean hasFileChanged() {
		return fileLastModified != scriptFile.lastModified();
	}
	

}
