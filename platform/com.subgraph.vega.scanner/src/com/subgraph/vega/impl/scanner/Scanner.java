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
package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;

import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.scanner.IScanProbeResult;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

public class Scanner implements IScanner {

	private IModel model;
	private IScanInstance currentScan;
	private IScannerConfig persistentConfig;
	
	private IWebCrawlerFactory crawlerFactory;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IScannerModuleRegistry moduleRegistry;
	private ScannerTask scannerTask;
	private Thread scannerThread;
	private IWorkspace currentWorkspace;
	private IContentAnalyzerFactory contentAnalyzerFactory;
	private List<IResponseProcessingModule> responseProcessingModules;
	private List<IBasicModuleScript> basicModules;
	private List<IScannerModule> allModules;
	
	protected void activate() {
		currentWorkspace = model.addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent)
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				else if(event instanceof WorkspaceCloseEvent)
					handleWorkspaceClose((WorkspaceCloseEvent) event);				
			}
		});
		reloadModules();
	}
	
	private void reloadModules() {
		if(responseProcessingModules == null || basicModules == null) {
			responseProcessingModules = moduleRegistry.getResponseProcessingModules();
			basicModules = moduleRegistry.getBasicModules();
		} else {
			responseProcessingModules = moduleRegistry.updateResponseProcessingModules(responseProcessingModules);
			basicModules = moduleRegistry.updateBasicModules(basicModules);
		}
		allModules = new ArrayList<IScannerModule>();
		allModules.addAll(responseProcessingModules);
		allModules.addAll(basicModules);
	}
	
	private void resetModuleTimestamps() {
		for(IScannerModule m: allModules) {
			m.getRunningTimeProfile().reset();
		}
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		this.currentWorkspace = event.getWorkspace();
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		this.currentWorkspace = null;
	}
	
	protected void deactivate() {
		
	}

	IWebCrawlerFactory getCrawlerFactory() {
		return crawlerFactory;
	}
		
	IModel getModel() {
		return model;
	}
	
	@Override
	public IScannerConfig getScannerConfig() {
		return persistentConfig;
	}
	
	@Override
	public List<IScannerModule> getAllModules() {
		reloadModules();
		return allModules;
	}

	@Override
	public IScannerConfig createScannerConfig() {
		return new ScannerConfig();
	}

	@Override 
	public void setScannerConfig(IScannerConfig config) {
		persistentConfig = config;
	}	
	
	@Override
	public IScanProbeResult probeTargetURI(URI uri) {
		final HttpClient client = requestEngineFactory.createUnencodingClient();
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(client, requestEngineFactory.createConfig() );
		final ScanProbe probe = new ScanProbe(uri, requestEngine);
		return probe.runProbe();
	}

	@Override
	public synchronized void startScanner(IScannerConfig config) {
		if(currentScan != null && currentScan.getScanStatus() != IScanInstance.SCAN_COMPLETED && currentScan.getScanStatus() != IScanInstance.SCAN_CANCELLED) {
			throw new IllegalStateException("Scanner is already running.  Verify scanner is not running with getScannerStatus() before trying to start.");
		}
		
		if(config.getBaseURI() == null)
			throw new IllegalArgumentException("Cannot start scan because no baseURI was specified");
		
		IHttpRequestEngineConfig requestEngineConfig = requestEngineFactory.createConfig();
		if (config.getCookieList() != null) {
			CookieStore cookieStore = requestEngineConfig.getCookieStore();
			for (Cookie c: config.getCookieList()) {
				cookieStore.addCookie(c);
			}
		}
		
		if(config.getMaxRequestsPerSecond() > 0) {
			requestEngineConfig.setRequestsPerMinute(config.getMaxRequestsPerSecond() * 60);
		}
		
		requestEngineConfig.setMaxConnections(config.getMaxConnections());
		requestEngineConfig.setMaxConnectionsPerRoute(config.getMaxConnections());
		requestEngineConfig.setMaximumResponseKilobytes(config.getMaxResponseKilobytes());
		final HttpClient client = requestEngineFactory.createUnencodingClient();
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(client, requestEngineConfig);
		reloadModules();
		resetModuleTimestamps();
		currentWorkspace.lock();
		
		currentScan = currentWorkspace.getScanAlertRepository().createNewScanInstance();
		scannerTask = new ScannerTask(currentScan, this, config, requestEngine, 
				currentWorkspace, contentAnalyzerFactory.createContentAnalyzer(currentScan), 
				responseProcessingModules, basicModules);
		scannerThread = new Thread(scannerTask);
		currentWorkspace.getScanAlertRepository().setActiveScanInstance(currentScan);
		currentScan.updateScanStatus(IScanInstance.SCAN_STARTING);
		scannerThread.start();
	}

	@Override
	public void stopScanner() {
		if(scannerTask != null)
			scannerTask.stop();
	}

	@Override
	public void runDomTests() {
		moduleRegistry.runDomTests();
	}
	
	protected void setCrawlerFactory(IWebCrawlerFactory crawlerFactory) {
		this.crawlerFactory = crawlerFactory;
	}
	
	protected void unsetCrawlerFactory(IWebCrawlerFactory crawlerFactory) {
		this.crawlerFactory = null;
	}
	
	protected void setRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = factory;
	}
	
	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
	}
	
	protected void setModuleRegistry(IScannerModuleRegistry registry) {
		this.moduleRegistry = registry;
	}
	
	protected void unsetModuleRegistry(IScannerModuleRegistry registry) {
		this.moduleRegistry = null;
	}
	
	protected void setModel(IModel model) {
		this.model = model;
	}
	
	protected void unsetModel(IModel model) {
		this.model = null;
	}
	
	protected void setContentAnalyzerFactory(IContentAnalyzerFactory factory) {
		this.contentAnalyzerFactory = factory;
	}

	protected void unsetContentAnalyzerFactory(IContentAnalyzerFactory factory) {
		this.contentAnalyzerFactory = null;
	}
}
