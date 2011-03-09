package com.subgraph.vega.impl.scanner.modules;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerMountPointModule;
import com.subgraph.vega.api.scanner.modules.IPerResourceScannerModule;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;
import com.subgraph.vega.impl.scanner.modules.internal.InternalModuleManager;
import com.subgraph.vega.impl.scanner.modules.scripting.PerDirectoryScript;
import com.subgraph.vega.impl.scanner.modules.scripting.PerHostScript;
import com.subgraph.vega.impl.scanner.modules.scripting.PerMountPointScript;
import com.subgraph.vega.impl.scanner.modules.scripting.PerResourceScript;
import com.subgraph.vega.impl.scanner.modules.scripting.ResponseProcessorScript;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptLoader;
import com.subgraph.vega.impl.scanner.modules.scripting.ScriptedModule;
import com.subgraph.vega.impl.scanner.modules.scripting.tests.DomTestModule;
import com.subgraph.vega.impl.scanner.modules.scripting.tests.TestScriptLoader;

public class ScannerModuleRepository implements IScannerModuleRegistry {
	private final InternalModuleManager internalModules = new InternalModuleManager();
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
		try {
			if(!(configFile.exists() && configFile.canRead()))
				return null;
			Reader configReader = new FileReader(configFile);
			Properties configProperties = new Properties();
			configProperties.load(configReader);
			String pathProp = configProperties.getProperty("vega.scanner.datapath");
			
			if(pathProp != null) 
				return new File(pathProp, "scripts" + File.separator + "scanner");
			
		} catch (IOException e) {
			return null;
		}
		return null;
	}
	
	@Override
	
	public List<IScannerModule> getAllModules(boolean enabledOnly) {
		final List<IScannerModule> modules = new ArrayList<IScannerModule>();
		
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(enabledOnly && !m.getEnabledState())
				continue;
			if(m.getModuleType() == ModuleScriptType.PER_SERVER)
				modules.add(new PerHostScript(m));
			else if(m.getModuleType() == ModuleScriptType.PER_DIRECTORY) 
				modules.add(new PerDirectoryScript(m));
			else if(m.getModuleType() == ModuleScriptType.PER_RESOURCE)
				modules.add(new PerResourceScript(m));
			else if(m.getModuleType() == ModuleScriptType.RESPONSE_PROCESSOR)
				modules.add(new ResponseProcessorScript(m));
		}
		for(IScannerModule m: getInternalModules(enabledOnly))
			modules.add(m);

		return modules;

	}
	@Override
	public List<IPerHostScannerModule> getPerHostModules(boolean enabledOnly) {
		final List<IPerHostScannerModule> modules = new ArrayList<IPerHostScannerModule>();
		
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(enabledOnly && !m.getEnabledState())
				continue;
			if(m.getModuleType() == ModuleScriptType.PER_SERVER)
				modules.add(new PerHostScript(m));
		}
		return modules;
	}

	@Override
	public List<IPerDirectoryScannerModule> getPerDirectoryModules(boolean enabledOnly) {
		final List<IPerDirectoryScannerModule> modules = new ArrayList<IPerDirectoryScannerModule>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(enabledOnly && !m.getEnabledState())
				continue;
			if(m.getModuleType() == ModuleScriptType.PER_DIRECTORY)
				modules.add(new PerDirectoryScript(m));
		}
		return modules;
	}

	@Override
	public List<IPerResourceScannerModule> getPerResourceModules(boolean enabledOnly) {
		final List<IPerResourceScannerModule> modules = new ArrayList<IPerResourceScannerModule>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(enabledOnly && !m.getEnabledState())
				continue;
			if(m.getModuleType() == ModuleScriptType.PER_RESOURCE)
				modules.add(new PerResourceScript(m));
		}
		return modules;
	}

	@Override
	public List<IResponseProcessingModule> getResponseProcessingModules(boolean enabledOnly) {
		final List<IResponseProcessingModule> modules = new ArrayList<IResponseProcessingModule>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(enabledOnly && !m.getEnabledState())
				continue;
			if(m.getModuleType() == ModuleScriptType.RESPONSE_PROCESSOR)
				modules.add(new ResponseProcessorScript(m));
		}
		return modules;
	}
	

	@Override
	public List<IPerMountPointModule> getPerMountPointModules(boolean enabledOnly) {
		final List<IPerMountPointModule> modules = new ArrayList<IPerMountPointModule>();
		for(ScriptedModule m: scriptLoader.getAllModules()) {
			if(enabledOnly && !m.getEnabledState())
				continue;
			if(m.getModuleType() == ModuleScriptType.PER_MOUNTPOINT)
				modules.add(new PerMountPointScript(m));
		}
		return modules;
	}
	
	@Override
	public List<IScannerModule> getInternalModules(boolean enabledOnly) {
		return internalModules.getModules(enabledOnly);
	}

	@Override
	public void refreshModuleScripts() {
		scriptLoader.reloadModules();		
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
	public void resetAllModuleTimestamps() {
		for(IScannerModule m: getAllModules(true))
			m.getRunningTimeProfile().reset();
	}
}
