package com.subgraph.vega.impl.scanner.modules.scripting;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptLoader {
	private final Logger logger = Logger.getLogger("script-loader");
	private final Scriptable globalScope;
	private final File moduleRoot;
	private final PreludeLoader preludeLoader;
	private final ScriptCompiler moduleCompiler;
	private final List<ScriptedModule> allModules = new ArrayList<ScriptedModule>();
	
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
		preludeLoader.load();
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
	
	public void load() {
		allModules.clear();
		for(File scriptFile: allScriptFiles()) {
			ScriptedModule compileModule = compileModule(scriptFile);
			if(compileModule != null)
				allModules.add(compileModule);
		}
	}
	
	public List<ScriptedModule> getAllModules() {
		synchronized(allModules) {
			return Collections.unmodifiableList(allModules);
		}
	}
	public Scriptable getPreludeScope() {
		return preludeLoader.getPreludeScope();
	}
	
	public void refreshModules() {
		synchronized (allModules) {
			for(int i = 0; i < allModules.size(); i++) {
				ScriptedModule module = allModules.get(i);
				if(module.hasFileChanged()) {
					ScriptedModule newModule = compileModule(module.getScriptFile());
					if(newModule != null)
						allModules.set(i, newModule);
				}
			}
		}
	}
	
	private ScriptedModule compileModule(File scriptFile) {
		try {
			Scriptable module = moduleCompiler.compileFile(scriptFile);
			if(module == null)
				return null;
			ModuleValidator validator = new ModuleValidator(module);
			validator.validate();
			return new ScriptedModule(scriptFile, module, validator.getName(), validator.getType(), validator.getRunFunction());
		} catch (ModuleValidator.ModuleValidationException e) {
			logger.warning("Failed to validate module script "+ scriptFile.getAbsolutePath() +" :"+ e.getMessage());
			return null;
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Failed to compile module script.", e).toString());
			return null;
			
		}
		
		
	}
	private List<File> allScriptFiles() {
		final File scriptRoot = new File(moduleRoot, "modules");
		final List<File> scriptFiles = new ArrayList<File>();
		crawlDirectory(scriptRoot, scriptFiles);
		return scriptFiles;
	}
	
	private void crawlDirectory(File dir, List<File> files) {
		for(File f: dir.listFiles(scriptFilter))
			files.add(f);
		for(File d: dir.listFiles(directoryFilter))
			crawlDirectory(d, files);
	}
}
