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
package com.subgraph.vega.internal.http.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpProxyListener;
import com.subgraph.vega.api.http.proxy.IHttpProxyListenerConfig;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IHttpProxyServiceEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpProxyTransactionManipulator;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.requests.IRequestOriginProxy;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.internal.http.proxy.ssl.ProxySSLInitializationException;
import com.subgraph.vega.internal.http.proxy.ssl.SSLContextRepository;

public class HttpProxyService implements IHttpProxyService {
	private final Logger logger = Logger.getLogger(HttpProxyService.class.getName());
	private final List<IHttpProxyServiceEventHandler> eventHandlers;
	private boolean isRunning = false;
	private boolean isPassthrough = false;
	private IModel model;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IHttpRequestEngineConfig requestEngineConfig;
	private IContentAnalyzerFactory contentAnalyzerFactory;
	private IScannerModuleRegistry moduleRepository;
	private IScanner scanner;
	private CookieStore cookieStore;
	private IPathFinder pathFinder;
	private IContentAnalyzer contentAnalyzer;
	private List<IResponseProcessingModule> responseProcessingModules;
	private IWorkspace currentWorkspace;
	private Map<String, IHttpProxyListenerConfig> listenerConfigMap = new HashMap<String, IHttpProxyListenerConfig>();
	private Map<String, HttpProxyListener> listenerMap = new ConcurrentHashMap<String, HttpProxyListener>();
	private final IHttpInterceptProxyEventHandler listenerEventHandler;
	private final ProxyTransactionManipulator transactionManipulator;
	private ProxyScanner proxyScanner;
	private HttpInterceptor interceptor;
	private SSLContextRepository sslContextRepository;
	
	public HttpProxyService() {
		eventHandlers = new ArrayList<IHttpProxyServiceEventHandler>();
		listenerEventHandler = new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				processTransaction(transaction);
			}
		};
		transactionManipulator = new ProxyTransactionManipulator();
	}

	public void activate() {
		interceptor = new HttpInterceptor(model);
		cookieStore = new BasicCookieStore();
		proxyScanner = new ProxyScanner(scanner, cookieStore, model);

		try {
			sslContextRepository = SSLContextRepository.createInstance(pathFinder.getVegaDirectory());
		} catch (ProxySSLInitializationException e) {
			sslContextRepository = null;
			logger.warning("Failed to initialize SSL support in proxy.  SSL interception will be disabled. ("+ e.getMessage() + ")");
		}
	}

	@Override
	public void registerEventHandler(IHttpProxyServiceEventHandler handler) {
		eventHandlers.add(handler);
	}

	@Override
	public void unregisterEventHandler(IHttpProxyServiceEventHandler handler) {
		eventHandlers.remove(handler);
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public boolean isPassthrough() {
		synchronized(this) {
			return isPassthrough;
		}
	}

	@Override
	public IHttpProxyListenerConfig createListenerConfig() {
		return new HttpProxyListenerConfig();
	}

	@Override
	public void setListenerConfigs(IHttpProxyListenerConfig[] listenerConfigs) {
		// remove existing listeners that are not in the list
		for (Iterator<Map.Entry<String, IHttpProxyListenerConfig>> iter = listenerConfigMap.entrySet().iterator(); iter.hasNext();) {
			final Map.Entry<String, IHttpProxyListenerConfig> entry = iter.next();
			final String k = entry.getKey();
			int idx;
			for (idx = 0; idx < listenerConfigs.length; idx++) {
				if (k.compareTo(((IHttpProxyListenerConfig)listenerConfigs[idx]).toString()) == 0) {
					break;
				}
			}
			if (idx == listenerConfigs.length) {
				if (isRunning != false) {
					HttpProxyListener listener = listenerMap.remove(k); 
					stopListener(listener);
				}
				iter.remove();
			}
		}

		// create listeners for any new addresses
		for (int idx = 0; idx < listenerConfigs.length; idx++) {
			final IHttpProxyListenerConfig config = listenerConfigs[idx];
			final String key = config.toString();
			if (listenerConfigMap.get(key) == null) {
				listenerConfigMap.put(key, config);
				if (isRunning != false) {
					startListener(config);
				}
			}
		}

		// notify event handlers
		for (IHttpProxyServiceEventHandler handler: eventHandlers) {
			handler.notifyConfigChange(listenerMap.size());
		}
	}

	@Override
	public IHttpProxyListenerConfig[] getListenerConfigs() {
		return listenerMap.keySet().toArray(new IHttpProxyListenerConfig[0]);
	}

	@Override
	public IHttpProxyListener[] getListeners() {
		return listenerMap.values().toArray(new IHttpProxyListener[0]);
	}

	@Override
	public int getListenerConfigsCount() {
		return listenerMap.size();
	}

	@Override
	public void setPassthrough(boolean enabled) {
		synchronized(this) {
			isPassthrough = enabled;
			interceptor.setEnabled(!enabled);
		}
	}

	@Override
	public void start() {
		currentWorkspace = model.getCurrentWorkspace();
		if(currentWorkspace == null) 
			throw new IllegalStateException("Cannot start proxy because no workspace is currently open");
		currentWorkspace.lock();
		isRunning = true;

		responseProcessingModules = loadModules();
		contentAnalyzer = contentAnalyzerFactory.createContentAnalyzer(currentWorkspace.getScanAlertRepository().getProxyScanInstance());
		contentAnalyzer.setResponseProcessingModules(responseProcessingModules);
		contentAnalyzer.setDefaultAddToRequestLog(true);
		contentAnalyzer.setAddLinksToModel(true);

		proxyScanner.reloadModules();
		
		for (IHttpProxyListenerConfig config: listenerConfigMap.values()) {
			startListener(config);
		}

		for (IHttpProxyServiceEventHandler handler: eventHandlers) {
			handler.notifyStart(listenerMap.size());
		}
	}

	private void startListener(IHttpProxyListenerConfig config) {
		IRequestOriginProxy requestOrigin = currentWorkspace.getRequestLog().getRequestOriginProxy(config.getInetAddress(), config.getPort());
		IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(IHttpRequestEngine.EngineConfigType.CONFIG_PROXY, requestEngineConfig, requestOrigin);
		requestEngine.setCookieStore(cookieStore);
		HttpProxyListener listener = new HttpProxyListener(config, transactionManipulator, interceptor, requestEngine, sslContextRepository);
		listener.registerEventHandler(listenerEventHandler);
		listenerMap.put(config.toString(), listener);
		listener.start();
		for (IHttpProxyServiceEventHandler handler: eventHandlers) {
			handler.notifyStartListener(listener);
		}
	}
	
	private void stopListener(IHttpProxyListener listener) {
		listener.unregisterEventHandler(listenerEventHandler);
		listener.stop();
		for (IHttpProxyServiceEventHandler handler: eventHandlers) {
			handler.notifyStopListener(listener);
		}
	}
	
	private List<IResponseProcessingModule> loadModules() {
		if(responseProcessingModules == null) {
			return moduleRepository.getResponseProcessingModules();
		} else {
			return moduleRepository.updateResponseProcessingModules(responseProcessingModules);
		}
	}

	private void processTransaction(IProxyTransaction transaction) {
		synchronized(this) {
			if(transaction.getResponse() == null || contentAnalyzer == null || isPassthrough) {
				return;
			}
		}
		try {
			if(proxyScanner.isEnabled()) {
				proxyScanner.processRequest(transaction.getRequest());
			}
			contentAnalyzer.processResponse(transaction.getResponse());
		} catch (RuntimeException e) {
			logger.log(Level.WARNING, "Exception processing transaction response: "+ e.getMessage(), e);
		}
	}
	@Override
	public void stop() {
		if(currentWorkspace == null)
			throw new IllegalStateException("No workspace is open");
		isRunning = false;
		proxyScanner.setEnabled(false);

		for (Iterator<Map.Entry<String, HttpProxyListener>> iter = listenerMap.entrySet().iterator(); iter.hasNext();) {
			final Map.Entry<String, HttpProxyListener> entry = iter.next();
			stopListener(entry.getValue());
			iter.remove();
		}
		listenerMap.clear();

		contentAnalyzer = null;
		currentWorkspace.unlock();

		for (IHttpProxyServiceEventHandler handler: eventHandlers) {
			handler.notifyStop();
		}
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
		requestEngineConfig = requestEngineFactory.createConfig();
	}

	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
		requestEngineConfig = null;
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
	
	protected void setScanner(IScanner scanner) {
		this.scanner = scanner;
	}
	
	protected void unsetScanner(IScanner scanner) {
		this.scanner = null;
	}

	@Override
	public List<IResponseProcessingModule> getResponseProcessingModules() {
		responseProcessingModules = loadModules();
		if(responseProcessingModules == null) {
			return Collections.emptyList();
		} else {
			return responseProcessingModules;
		}
	}

	@Override
	public boolean isProxyScanEnabled() {
		return proxyScanner.isEnabled();
	}

	@Override
	public void setProxyScanEnabled(boolean enabled) {
		proxyScanner.setEnabled(enabled);
	}

	@Override
	public IScannerConfig getProxyScanConfig() {
		return proxyScanner.getConfig();
	}

	@Override
	public List<IBasicModuleScript> getProxyScanModules() {
		return proxyScanner.getInjectionModules();
	}
}
