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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.subgraph.vega.api.scanner.modules.ModuleScriptType;

public class ScriptedModule {
	private final ScriptFile scriptFile;
	
	private String categoryName;
	private String moduleName;
	private ModuleScriptType moduleType;
	private Function runFunction;
	private boolean isDisabledInScript;
	private boolean isDefaultEnabled;
	private boolean isTimeSensitive;
	private boolean isDifferential;

	public ScriptedModule(ScriptFile scriptFile, String category, ModuleValidator validator) {
		this.scriptFile = scriptFile;
		updateFromValidator(validator);
		categoryName = category;
	}

	public ScriptedModule(ScriptFile scriptFile, ModuleValidator validator) {
		
		this.scriptFile = scriptFile;
		updateFromValidator(validator);
	}
	
	public void updateFromValidator(ModuleValidator validator) {
		categoryName = validator.getCategoryName();
		moduleName = validator.getName();
		moduleType = validator.getType();
		runFunction = validator.getRunFunction();
		isDisabledInScript = validator.isDisabled();
		isDefaultEnabled = validator.isDefaultEnabled();
		isTimeSensitive = validator.isTimeSensitive();
		isDifferential = validator.isDifferential();
	}

	public Scriptable createInstanceScope(Context cx) {
		Scriptable scope = cx.newObject(scriptFile.getCompiledScript());
		scope.setPrototype(scriptFile.getCompiledScript());
		scope.setParentScope(null);
		return scope;
	}
	
	public void runModule(Context cx, Scriptable instanceScope) {
		runModule(cx, instanceScope, new Object[0]);
	}

	public void runModule(Context cx, Scriptable instanceScope, Object[] arguments) {
		runFunction.call(cx, instanceScope, instanceScope, arguments);
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
	
	public boolean isDifferential() {
		return isDifferential;
	}

	public boolean isTimeSensitive() {
		return isTimeSensitive;
	}

	public ModuleScriptType getModuleType() {
		return moduleType;
	}
	
	public Scriptable getModuleScope() {
		return scriptFile.getCompiledScript();
	}
	
	public boolean isDefaultEnabled() {
		return isDefaultEnabled;
	}

	public boolean isDisabled() {
		return isDisabledInScript;
	}
}
