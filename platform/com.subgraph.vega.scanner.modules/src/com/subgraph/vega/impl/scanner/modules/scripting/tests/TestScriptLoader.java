package com.subgraph.vega.impl.scanner.modules.scripting.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.osgi.framework.Bundle;

import com.subgraph.vega.impl.scanner.modules.scripting.ModuleValidator;
import com.subgraph.vega.impl.scanner.modules.scripting.ModuleValidator.ModuleValidationException;
import com.subgraph.vega.impl.scanner.modules.scripting.RhinoExceptionFormatter;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptCompiler;
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
			ScriptedModule compiledModule = compileModule(scriptURL);
			if(compiledModule != null)
				allModules.add(compiledModule);
		}
	}
	
	public List<ScriptedModule> getAllModules() {
		return Collections.unmodifiableList(allModules);
	}
	
	public void refreshModules() {
		synchronized (allModules) {
			for(int i = 0; i < allModules.size(); i++) {
				ScriptedModule module = allModules.get(i);
				ScriptedModule newModule = compileModule(module.getScriptURL());
				if(newModule != null)
					allModules.set(i, newModule);
			}
		}
	}
	
	private ScriptedModule compileModule(URL scriptURL) {
		try {
			InputStream input = scriptURL.openStream();
			Reader r = new InputStreamReader(input);
			Scriptable module = moduleCompiler.compileReader(r, scriptURL.getPath());
			if(module  == null)
				return null;
			ModuleValidator validator = new ModuleValidator(module);
			validator.validate();
			return new ScriptedModule(scriptURL, module, validator.getName(), validator.getType(), validator.getRunFunction());
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Failed to compile module script.", e).toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModuleValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
