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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.subgraph.vega.api.scanner.modules.ModuleScriptType;
import com.subgraph.vega.impl.scanner.modules.scripting.ModuleValidator.ModuleValidationException;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptFile.CompileStatus;

public class ScriptLoader {
	private final Logger logger = Logger.getLogger("script-loader");
	private final Scriptable globalScope;
	private final File moduleRoot;
	private final PreludeLoader preludeLoader;
	private final ScriptCompiler moduleCompiler;
	private final boolean preludeLoadFailed;
	
	private final Map<File, ScriptedModule> modulePathMap = new HashMap<File, ScriptedModule>();
	
	/* Script files are tracked separately from modules mainly so that we can track files have failed to compile. */
	private final Map<File, ScriptFile> scriptPathMap = new HashMap<File, ScriptFile>();
	
	private final FileFilter scriptFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".js");
		}
	};
	
	private final FileFilter directoryFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};
	
	public ScriptLoader(File moduleRoot) {
		logger.info("Loading scripts from "+ moduleRoot.getAbsolutePath());
		this.moduleRoot = moduleRoot;
		enableDynamicScope();
		globalScope = createGlobalScope();
		preludeLoader = new PreludeLoader(new File(moduleRoot, "prelude"), globalScope);
		preludeLoadFailed = (preludeLoader.load() == false);
		if(preludeLoadFailed)
			moduleCompiler = null;
		else
			moduleCompiler = new ScriptCompiler(preludeLoader.getPreludeScope());
	}
	
	private void enableDynamicScope() {
		ContextFactory.initGlobal(new ContextFactory() {
			@Override
			protected boolean hasFeature(Context cx, int featureIndex) {
				if(featureIndex == Context.FEATURE_DYNAMIC_SCOPE)
					return true;
				else
					return super.hasFeature(cx, featureIndex);
			}
		});
	}
	
	private Scriptable createGlobalScope() {
		try {
			final Context cx = Context.enter();
			final ScriptableObject importer = new ImporterTopLevel(cx, true);
			return cx.initStandardObjects(importer, true);
		} finally {
			Context.exit();
		}
	}
	
	public List<ScriptedModule> getAllModulesByType(ModuleScriptType type) {
		final List<ScriptedModule> result = new ArrayList<ScriptedModule>();
		synchronized(modulePathMap) {
			for(ScriptedModule m: modulePathMap.values()) {
				if(!m.isDisabled() && (type == null || type == m.getModuleType())) {
					result.add(m);
				}
			}
		}
		return result;
	}

	public List<ScriptedModule> getAllModules() {
		return getAllModulesByType(null);
	}
	
	public Scriptable getPreludeScope() {
		return preludeLoader.getPreludeScope();
	}

	private ScriptedModule compileModuleScript(ScriptFile scriptFile) {
		final ModuleValidator validator = compileAndValidate(scriptFile);
		if(validator == null) {
			return null;
		}
		return new ScriptedModule(scriptFile, validator);
	}
	
	private boolean recompileModule(ScriptedModule module) {
		final ModuleValidator validator = compileAndValidate(module.getScriptFile());
		if(validator == null)
			return false;
		module.updateFromValidator(validator);
		return true;
	}
	
	private ModuleValidator compileAndValidate(ScriptFile scriptFile) {
		if(!moduleCompiler.compile(scriptFile) || scriptFile.getCompileStatus() != CompileStatus.COMPILE_SUCCEEDED) {
			logger.warning(scriptFile.getCompileFailureMessage());
			return null;
		}
		return validateModule(scriptFile.getCompiledScript(), scriptFile.getPath());
	}

	private ModuleValidator validateModule(Scriptable module, String modulePath) {
		final ModuleValidator validator = new ModuleValidator(module);
		try {
			validator.validate();
			return validator;
		} catch (ModuleValidationException e) {
			logger.warning("Failed to validate module script "+ modulePath +" :"+ e.getMessage());
			return null;
		}
	}
	
	private List<File> allScriptPaths() {
		final File scriptRoot = new File(moduleRoot, "modules");
		final List<File> scriptFiles = new ArrayList<File>();
		crawlDirectory(scriptRoot, scriptFiles);
		return scriptFiles;
	}
	
	private void crawlDirectory(File dir, List<File> files) {
		for(File f: dir.listFiles(scriptFilter)) {
			files.add(f);
		}
		
		for(File d: dir.listFiles(directoryFilter)) {
			crawlDirectory(d, files);
		}
	}
	
	public boolean reloadModules() {
		boolean somethingChanged = false;
		if(preludeLoadFailed) {
			return false;
		}

		synchronized(modulePathMap) {
			synchronizeScriptPaths();
			for(Map.Entry<File, ScriptFile> entry: scriptPathMap.entrySet()) {
				if(compileScriptFileIfNeeded(entry.getKey(), entry.getValue())) {
					somethingChanged = true;
				}
			}
		}
		return somethingChanged;
	}

	private void synchronizeScriptPaths() {
		final Set<File> pathSet = new HashSet<File>();

		for(File path: allScriptPaths()) {
			pathSet.add(path);
			if(!scriptPathMap.containsKey(path)) { 
				scriptPathMap.put(path, new ScriptFile(path));
			}
		}

		final List<File> keys = new ArrayList<File>(scriptPathMap.keySet());
		for(File path: keys) {
			if(!pathSet.contains(path)) {
				modulePathMap.remove(path);
				scriptPathMap.remove(path);
			}
		}
	}

	private boolean compileScriptFileIfNeeded(File path, ScriptFile scriptFile) {
		if(!isCompileNeeded(scriptFile)) {
			return false;
		}

		if(modulePathMap.containsKey(path)) {
			if(!recompileModule(modulePathMap.get(path))) {
				modulePathMap.remove(path);
			}
		} else {
			final ScriptedModule module = compileModuleScript(scriptFile);
			if(module != null) {
				modulePathMap.put(path, module);
			}
		}
		return true;
	}

	private boolean isCompileNeeded(ScriptFile scriptFile) {
		return ((scriptFile.getCompileStatus() == CompileStatus.NOT_COMPILED) || scriptFile.hasFileChanged());
	}
}
