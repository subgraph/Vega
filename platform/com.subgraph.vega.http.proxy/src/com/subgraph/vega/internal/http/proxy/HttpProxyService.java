package com.subgraph.vega.internal.http.proxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IHttpProxyTransactionManipulator;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.internal.http.proxy.ssl.ProxySSLInitializationException;
import com.subgraph.vega.internal.http.proxy.ssl.SSLContextRepository;

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
	private IPathFinder pathFinder;

	private ProxyTransactionManipulator transactionManipulator;
	private HttpInterceptor interceptor;
	private SSLContextRepository sslContextRepository;

	public HttpProxyService() {
		eventHandler = new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				processTransaction(transaction);
			}
		};

		transactionManipulator = new ProxyTransactionManipulator(); 
	}

	public void activate() {
		interceptor = new HttpInterceptor(model);
		try {
			sslContextRepository = SSLContextRepository.createInstance(pathFinder.getVegaDirectory());
		} catch (ProxySSLInitializationException e) {
			sslContextRepository = null;
			logger.warning("Failed to initialize SSL support in proxy.  SSL interception will be disabled. ("+ e.getMessage() + ")");
		}
	}

	@Override
	public void start(int proxyPort) {
		currentWorkspace = model.getCurrentWorkspace();
		if(currentWorkspace == null) 
			throw new IllegalStateException("Cannot start proxy because no workspace is currently open");
		currentWorkspace.lock();
		contentAnalyzer = contentAnalyzerFactory.createContentAnalyzer(currentWorkspace.getScanAlertRepository().getProxyScanInstance());
		contentAnalyzer.setResponseProcessingModules(moduleRepository.getResponseProcessingModules(true));
		contentAnalyzer.setDefaultAddToRequestLog(true);
		contentAnalyzer.setAddLinksToModel(true);
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		proxy = new HttpProxy(proxyPort, transactionManipulator, interceptor, requestEngine, sslContextRepository);
		proxy.registerEventHandler(eventHandler);
		proxy.startProxy();
	}

	private void processTransaction(IProxyTransaction transaction) {
		if(transaction.getResponse() == null || contentAnalyzer == null)
			return;
		try {
			contentAnalyzer.processResponse(transaction.getResponse());
		} catch (RuntimeException e) {
			logger.log(Level.WARNING, "Exception processing transaction response: "+ e.getMessage(), e);
		}
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
	public IHttpProxyTransactionManipulator getTransactionManipulator() {
		return transactionManipulator;
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

	protected void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}

	protected void unsetPathFinder(IPathFinder pathFinder) {
		this.pathFinder = null;
	}

}
