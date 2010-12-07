package com.subgraph.vega.internal.crawler;

import java.net.URI;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.urls.IUrlExtractor;

public class WebCrawlerFactory implements IWebCrawlerFactory {

	private IModel model;
	private IUrlExtractor urlExtractor;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IWorkspace currentWorkspace;
	
	@Override
	public IWebCrawler create(ICrawlerConfig config) {
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		return create(config, requestEngine);
	}
	
	@Override
	public IWebCrawler create(ICrawlerConfig config, IHttpRequestEngine requestEngine) {
		currentWorkspace.lock();
		return new WebCrawler(currentWorkspace, urlExtractor, requestEngine, config);
	}

	
	public ICrawlerConfig createBasicConfig(URI baseURI) {
		return new BasicCrawlerConfig(baseURI);
	}
	
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
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		currentWorkspace = event.getWorkspace();
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		currentWorkspace = null;
	}
	protected void setModel(IModel model) {
		this.model = model;
	}
	
	protected void unsetModel(IModel model) {
		this.model = null;
	}
	
	protected void setUrlExtractor(IUrlExtractor extractor) {
		this.urlExtractor = extractor;
	}
	
	protected void unsetUrlExtractor(IUrlExtractor extractor) {
		this.urlExtractor = null;
	}
	
	protected void setRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = factory;
	}
	
	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
	}
}
