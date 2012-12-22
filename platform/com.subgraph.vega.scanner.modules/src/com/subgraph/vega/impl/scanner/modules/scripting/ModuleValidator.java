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

public class ModuleValidator {
	
	private final Scriptable moduleScope;
	private String moduleName;
	private String categoryName;
	private ModuleScriptType moduleType;
	private Function runFunction;
	private boolean isValidated;
	private boolean isDisabled;
	private boolean isDefaultDisabled;
	private boolean isTimeSensitive;
	private boolean isDifferential;
	
	public ModuleValidator(Scriptable moduleScope) {
		this.moduleScope = moduleScope;
	}
	
	public static class ModuleValidationException extends Exception {
		private static final long serialVersionUID = 1L;
		ModuleValidationException(String message) {
			super(message);
		}
	}
	
	public void validate() throws ModuleValidationException {
		if(isValidated)
			return;
		final Scriptable moduleObject = getModule();
		moduleName = getStringFromModuleObject(moduleObject, "name");
		moduleType = getScriptType(moduleObject);
		if(hasStringInModuleObject(moduleObject, "category"))
			categoryName = getStringFromModuleObject(moduleObject, "category");
		else
			categoryName = moduleType.getVerboseName();
		
		runFunction = getEntryFunction();
		isDisabled = getFlagFromModuleObject(moduleObject, "disabled");
		isDefaultDisabled = getFlagFromModuleObject(moduleObject, "defaultDisabled");
		isTimeSensitive = getFlagFromModuleObject(moduleObject, "timeSensitive");
		isDifferential = getFlagFromModuleObject(moduleObject, "differential");
		isValidated = true;
	}
	
	public String getName() {
		if(!isValidated)
			throw new IllegalStateException("Cannot get name because module is not validated");
		return moduleName;
	}
	
	public String getCategoryName() {
		if(!isValidated)
			throw new IllegalStateException("Cannot get category name because module is not validated");
		return categoryName;
	}

	public boolean isDisabled() {
		if(!isValidated)
			throw new IllegalStateException("Cannot get disabled flag because module is not validated");
		return isDisabled;
	}
	
	public boolean isDefaultEnabled() {
		if(!isValidated) 
			throw new IllegalStateException("Cannot get default enabled flag because module is not validated");

		return !isDefaultDisabled;
	}

	public boolean isTimeSensitive() {
		if(!isValidated) { 
			throw new IllegalStateException("Cannot get time sensitive flag because module is not validated");
		}
		return isTimeSensitive;
	}

	public boolean isDifferential() {
		if(!isValidated) {
			throw new IllegalStateException("Cannot get differential flag because module is not validated");
		}
		return isDifferential;
	}

	public ModuleScriptType getType() {
		if(!isValidated)
			throw new IllegalStateException("Cannot get type because module is not validated");
		return moduleType;
	}
	
	public Function getRunFunction() {
		if(!isValidated)
			throw new IllegalStateException("Cannot get run function because module is not validated");
		return runFunction;
	}
	
	private Scriptable getModule() throws ModuleValidationException {
		final Object ob = moduleScope.get("module", moduleScope);
		if(ob == Scriptable.NOT_FOUND) 
			throw new ModuleValidationException("No 'module' object found.");
		return Context.toObject(ob, moduleScope);
	}
	
	private ModuleScriptType getScriptType(Scriptable module) throws ModuleValidationException {
		if(!hasStringInModuleObject(module, "type"))
			return ModuleScriptType.BASIC_MODULE;
		
		final String typeName = getStringFromModuleObject(module, "type");
		final ModuleScriptType type = ModuleScriptType.lookup(typeName);
		if(type == null) 
			throw new ModuleValidationException("Unrecognized module type: "+ typeName);
		else
			return type;
	}
	
	private boolean hasStringInModuleObject(Scriptable module, String name) {
		final Object ob = module.get(name, moduleScope);
		return (ob != Scriptable.NOT_FOUND && (ob instanceof String));
			
	}
	private String getStringFromModuleObject(Scriptable module, String name) throws ModuleValidationException {
		final Object ob = module.get(name, moduleScope);
		if(ob == Scriptable.NOT_FOUND) 
			throw new ModuleValidationException("Could not find module property '"+ name +"'.");
		if(!(ob instanceof String)) 
			throw new ModuleValidationException("Module property '"+ name +"' is not a string type as expected.");
		return (String) ob;
	}
	
	private boolean getFlagFromModuleObject(Scriptable module, String name) {
		final Object ob = module.get(name, moduleScope);
		return !(ob == Scriptable.NOT_FOUND);
	}
	
	private Function getEntryFunction() throws ModuleValidationException {
		Function entry = getGlobalFunction("run");
		if(entry == null) 
			entry = getGlobalFunction("initialize");
		if(entry == null)
			throw new ModuleValidationException("Could not find global entry function 'run()' or 'initialize()' in module.");
		return entry;
	}
	
	private Function getGlobalFunction(String name) throws ModuleValidationException {
		final Object ob = moduleScope.get(name, moduleScope);
		if(ob == Scriptable.NOT_FOUND)
			return null;
		if(!(ob instanceof Function))
			throw new ModuleValidationException("Global identifier '"+ name +"' is not a function as expected");
		return (Function) ob;
	}
}
