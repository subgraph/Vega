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
package com.subgraph.vega.impl.scanner.modules;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;
import com.subgraph.vega.impl.scanner.modules.scripting.BasicModuleScript;
import com.subgraph.vega.impl.scanner.modules.scripting.ResponseProcessorScript;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptLoader;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptedModule;
import com.subgraph.vega.impl.scanner.modules.scripting.tests.DomTestModule;
import com.subgraph.vega.impl.scanner.modules.scripting.tests.TestScriptLoader;

public class ScannerModuleRepository implements IScannerModuleRegistry {
	private final Logger logger = Logger.getLogger("modules");
	private IPathFinder pathFinder;
	private IHTMLParser htmlParser;
	private IModel model;
	private ScriptLoader scriptLoader;
	private TestScriptLoader testScriptLoader;
	private Bundle bundle;
	private IWorkspace currentWorkspace;

	void activate(BundleContext context) {
		this.bundle = context.getBundle();
		scriptLoader = new ScriptLoader(getScriptDirectory());
		scriptLoader.reloadModules();
		currentWorkspace = model.addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent)
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				else if(event instanceof WorkspaceCloseEvent)
					handleWorkspaceClose((WorkspaceCloseEvent) event);
			}
		});
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		this.currentWorkspace = event.getWorkspace();
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		this.currentWorkspace = null;
	}

	private File getScriptDirectory() {
		final File configScriptPath = getScriptDirectoryFromConfig(pathFinder.getConfigFilePath());
		if(configScriptPath != null && configScriptPath.exists() && configScriptPath.isDirectory())
			return configScriptPath;

		return new File(pathFinder.getDataDirectory(), "scripts" + File.separator + "scanner");
	}

	private File getScriptDirectoryFromConfig(File configFile) {
		Reader configReader = null;
		try {
			if(!(configFile.exists() && configFile.canRead())) {
				return null;
			}
			configReader = new FileReader(configFile);
			Properties configProperties = new Properties();
			configProperties.load(configReader);
			String pathProp = configProperties.getProperty("vega.scanner.datapath");

			if(pathProp != null) {
				return new File(pathProp, "scripts" + File.separator + "scanner");
			}

		} catch (IOException e) {
			logger.log(Level.WARNING, "I/O error opening config file "+ configFile.getAbsolutePath(), e);
		} finally {
			try {
				if(configReader != null) {
					configReader.close();
				}
			} catch (IOException e) {
				logger.log(Level.WARNING, "I/O error closing config file "+ configFile.getAbsolutePath(), e);
			}
		}
		return null;
	}

	@Override
	public List<IResponseProcessingModule> getResponseProcessingModules() {
		final List<IResponseProcessingModule> modules = new ArrayList<IResponseProcessingModule>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(m.getModuleType() == ModuleScriptType.RESPONSE_PROCESSOR)
				modules.add(new ResponseProcessorScript(m));
		}
		return modules;
	}

	@Override
	public List<IBasicModuleScript> getBasicModules() {
		final List<IBasicModuleScript> modules = new ArrayList<IBasicModuleScript>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(m.getModuleType() == ModuleScriptType.BASIC_MODULE)
				modules.add(new BasicModuleScript(m));
		}
		return modules;
	}

	protected void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}

	protected void unsetPathFinder(IPathFinder pathFinder) {
		this.pathFinder = null;
	}

	protected void setHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = htmlParser;
	}

	protected void unsetHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = null;
	}

	protected void setModel(IModel model) {
		this.model = model;
	}

	protected void unsetModel(IModel model) {
		this.model = null;
	}

	@Override
	public void runDomTests() {
		if(testScriptLoader == null) {
			testScriptLoader = new TestScriptLoader(scriptLoader.getPreludeScope(),bundle);
			testScriptLoader.load();
		}

		Thread testThread = new Thread(createDomTestRunnable());
		testThread.start();
	}

	private Runnable createDomTestRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				for(ScriptedModule m: testScriptLoader.getAllModules()) {
					runDomTestModule(m);
				}
			}
		};
	}

	private void runDomTestModule(ScriptedModule module) {
		if(module.getModuleType() != ModuleScriptType.DOM_TEST)
			return;
		final DomTestModule test = new DomTestModule(module, bundle, htmlParser);
		try {
			test.run(currentWorkspace);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<IResponseProcessingModule> updateResponseProcessingModules(List<IResponseProcessingModule> currentModules) {
		if(!scriptLoader.reloadModules()) {
			return currentModules;
		}
		
		final Map<String, ResponseProcessorScript> pathMap = new LinkedHashMap<String, ResponseProcessorScript>();
		final List<IResponseProcessingModule> newModules = new ArrayList<IResponseProcessingModule>();
		
		for(IResponseProcessingModule m: currentModules) {
			if(m instanceof ResponseProcessorScript) {
				ResponseProcessorScript rps = (ResponseProcessorScript) m;
				pathMap.put(rps.getModule().getScriptFile().getPath(), rps);
			}
		}
		
		for(ScriptedModule sm: scriptLoader.getAllModulesByType(ModuleScriptType.RESPONSE_PROCESSOR)) {
			String path = sm.getScriptFile().getPath();
			if(pathMap.containsKey(path)) {
				ResponseProcessorScript old = pathMap.get(path);
				newModules.add(new ResponseProcessorScript(sm, old.isEnabled(), old.getRunningTimeProfile()));
			} else {
				newModules.add(new ResponseProcessorScript(sm));
			}
		}
		return newModules;
	}

	@Override
	public List<IBasicModuleScript> updateBasicModules(List<IBasicModuleScript> currentModules) {
		if(!scriptLoader.reloadModules()) {
			return currentModules;
		}
		
		final Map<String, BasicModuleScript> pathMap = new LinkedHashMap<String, BasicModuleScript>();
		final List<IBasicModuleScript> newModules = new ArrayList<IBasicModuleScript>();
		
		for(IBasicModuleScript m: currentModules) {
			if(m instanceof BasicModuleScript) {
				BasicModuleScript bms = (BasicModuleScript) m;
				pathMap.put(bms.getModule().getScriptFile().getPath(), bms);
			}
		}
		
		for(ScriptedModule sm: scriptLoader.getAllModulesByType(ModuleScriptType.BASIC_MODULE)) {
			String path = sm.getScriptFile().getPath();
			if(pathMap.containsKey(path)) {
				BasicModuleScript old = pathMap.get(path);
				newModules.add(new BasicModuleScript(sm, old.isEnabled(), old.getRunningTimeProfile()));
			} else {
				newModules.add(new BasicModuleScript(sm));
			}
		}
		return newModules;
	}
}
