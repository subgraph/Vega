package com.subgraph.vega.internal.http.proxy;

import java.util.logging.Logger;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

public class HttpProxyService implements IHttpProxyService {

	private final Logger logger = Logger.getLogger(HttpProxyService.class.getName());
	private final IHttpInterceptProxyEventHandler eventHandler;
	private IModel model;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IContentAnalyzerFactory contentAnalyzerFactory;
	private IContentAnalyzer contentAnalyzer;

	private IScannerModuleRegistry moduleRepository;
	private HttpProxy proxy;
	private IWorkspace currentWorkspace;
	private final HttpInterceptor interceptor = new HttpInterceptor();

	public HttpProxyService() {
		eventHandler = new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				processTransaction(transaction);
			}
		};
	}
	
	public void activate() {
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

	@Override
	public void start(int proxyPort) {
		if(currentWorkspace == null) 
			throw new IllegalStateException("Cannot start proxy because no workspace is currently open");
		currentWorkspace.lock();
		contentAnalyzer = contentAnalyzerFactory.createContentAnalyzer();
		contentAnalyzer.setResponseProcessingModules(moduleRepository.getResponseProcessingModules(true));
		contentAnalyzer.setDefaultAddToRequestLog(true);
		contentAnalyzer.setAddLinksToModel(true);
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		proxy = new HttpProxy(proxyPort, interceptor, requestEngine);
		proxy.registerEventHandler(eventHandler);
		proxy.startProxy();
	}

	private void processTransaction(IProxyTransaction transaction) {
		if(transaction.getResponse() == null || contentAnalyzer == null)
			return;
		contentAnalyzer.processResponse(transaction.getResponse());
	}

	@Override
	public void stop() {
		if(currentWorkspace == null)
			throw new IllegalStateException("No workspace is open");
		proxy.unregisterEventHandler(eventHandler);
		proxy.stopProxy();
		contentAnalyzer = null;
		currentWorkspace.unlock();
	}

	@Override
	public IHttpInterceptor getInterceptor() {
		return interceptor;
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

	protected void setRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = factory;
	}

	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
	}
	
	protected void setModuleRepository(IScannerModuleRegistry moduleRepository) {
		this.moduleRepository = moduleRepository;
	}
	
	protected void unsetModuleRepository(IScannerModuleRegistry moduleRepository) {
		this.moduleRepository = null;
	}
}
