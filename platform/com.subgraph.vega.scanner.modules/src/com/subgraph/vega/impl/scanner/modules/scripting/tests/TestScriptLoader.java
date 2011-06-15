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
package com.subgraph.vega.impl.scanner.modules.scripting.tests;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import org.mozilla.javascript.Scriptable;
import org.osgi.framework.Bundle;

import com.subgraph.vega.impl.scanner.modules.scripting.ModuleValidator;
import com.subgraph.vega.impl.scanner.modules.scripting.ModuleValidator.ModuleValidationException;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptCompiler;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptFile;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptFile.CompileStatus;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptedModule;

public class TestScriptLoader {
	private final Logger logger = Logger.getLogger("script-loader");
	private final QunitLoader qunitLoader;
	private final ScriptCompiler moduleCompiler;
	private final Bundle bundle;
	private final List<ScriptedModule> allModules = new ArrayList<ScriptedModule>();
	
	
	public TestScriptLoader(Scriptable globalScope, Bundle bundle) {
		this.bundle = bundle;
		qunitLoader = new QunitLoader(globalScope, bundle);
		qunitLoader.load();
		moduleCompiler = new ScriptCompiler(qunitLoader.getScope());
	}
	
	public void load() {
		allModules.clear();
		for(URL scriptURL: allTestScripts()) {
			ScriptedModule compiledModule = compileModule(new ScriptFile(scriptURL));
			if(compiledModule != null && !compiledModule.isDisabled())
				allModules.add(compiledModule);
		}
	}
	
	public List<ScriptedModule> getAllModules() {
		return Collections.unmodifiableList(allModules);
	}
	
	private ScriptedModule compileModule(ScriptFile scriptFile) {	
		if(!moduleCompiler.compile(scriptFile) || scriptFile.getCompileStatus() != CompileStatus.COMPILE_SUCCEEDED) {
			logger.warning(scriptFile.getCompileFailureMessage());
			return null;
		}
			
		final ModuleValidator validator = validateModule(scriptFile.getCompiledScript(), scriptFile.getPath());
		if(validator == null)
			return null;
			
		return new ScriptedModule(scriptFile, "Test Modules", validator);
	}
	
	private ModuleValidator validateModule(Scriptable module, String modulePath) {
		final ModuleValidator validator = new ModuleValidator(module);
		try {
			validator.validate();
			return validator;
		} catch (ModuleValidationException e) {
			logger.warning("Failed to validate test module "+ modulePath +" :"+ e.getMessage());
			return null;
		}
	}

	private List<URL> allTestScripts() {
		List<URL> scriptURLs = new ArrayList<URL>();
		Enumeration<?> entries = bundle.findEntries("/tests/scripts", "*", true);
		while(entries.hasMoreElements()) {
			URL u = (URL) entries.nextElement();
			scriptURLs.add(u);
		}
		return scriptURLs;
	}
}
