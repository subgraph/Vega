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

import org.apache.http.client.CookieStore;

import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.scanner.IProxyScan;
import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

public class Scanner implements IScanner {
	private IModel model;
	private IWebCrawlerFactory crawlerFactory;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IScannerModuleRegistry moduleRegistry;
	private IWorkspace currentWorkspace;
	private IContentAnalyzerFactory contentAnalyzerFactory;

	protected void activate() {
		currentWorkspace = model.addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if (event instanceof WorkspaceOpenEvent) {
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				} else if (event instanceof WorkspaceCloseEvent) {
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				} else if (event instanceof WorkspaceResetEvent) {
					handleWorkspaceReset((WorkspaceResetEvent) event);
				}
			}
		});
	}

	protected void deactivate() {
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		this.currentWorkspace = event.getWorkspace();
	}

	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		this.currentWorkspace = event.getWorkspace();
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		this.currentWorkspace = null;
	}

	@Override
	public IScan createScan() {
		return Scan.createScan(this, currentWorkspace);
	}
	
	@Override
	public IProxyScan createProxyScan(IWorkspace workspace, CookieStore cookieStore) {
		return new ProxyScan(workspace, cookieStore, this);
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

	public IWebCrawlerFactory getWebCrawlerFactory() {
		return crawlerFactory;
	}
	
	public IHttpRequestEngineFactory getHttpRequestEngineFactory() {
		return requestEngineFactory;
	}

	public IScannerModuleRegistry getScannerModuleRegistry() {
		return moduleRegistry;
	}
	
	public IModel getModel() {
		return model;
	}
	
	public IContentAnalyzerFactory getContentAnalyzerFactory() {
		return contentAnalyzerFactory;
	}

}
