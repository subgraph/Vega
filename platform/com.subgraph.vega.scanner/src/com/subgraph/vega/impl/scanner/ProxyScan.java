package com.subgraph.vega.impl.scanner;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.requests.IRequestOriginScanner;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.IProxyScan;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.api.util.VegaURI;
import com.subgraph.vega.impl.scanner.urls.UriFilter;
import com.subgraph.vega.impl.scanner.urls.UriParser;

public class ProxyScan implements IProxyScan {
	private final Logger logger = Logger.getLogger("scanner");
	private final IWorkspace workspace;
	private final CookieStore cookieStore;
	private final Scanner scanner;
	private final IScanInstance scanInstance;
	private final IScannerConfig config;
	private  IWebCrawler crawler;
	private UriParser uriParser;
	
	private List<IBasicModuleScript> basicModules;
	private Object startLock = new Object();
	private boolean isStarted = false;
	
	ProxyScan(IWorkspace workspace, CookieStore cookieStore, Scanner scanner) {
		this.workspace = workspace;
		this.cookieStore = cookieStore;
		this.scanner = scanner;
		this.scanInstance = workspace.getScanAlertRepository().getScanInstanceByScanId(IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID);
		this.config = new ScannerConfig();
		reloadModules();
		logger.setLevel(Level.ALL);
	}

	@Override
	public IScannerConfig getConfig() {
		return config;
	}

	@Override
	public void scanGetTarget(VegaURI target, List<NameValuePair> parameters) {
		synchronized (startLock) {
			if(!isStarted) {
				start();
			}
		}
		final IPathState ps = uriParser.processUri(stripQuery(target));
		ps.maybeAddParameters(parameters);
	}

	@Override
	public void scanPostTarget(VegaURI target, List<NameValuePair> parameters) {
		synchronized (startLock) {
			if(!isStarted) {
				start();
			}
		}
		final IPathState ps = uriParser.processUri(stripQuery(target));
		ps.maybeAddPostParameters(parameters);
	}
	
	private VegaURI stripQuery(VegaURI uri) {
		if(uri.getQuery() != null) {
			return new VegaURI(uri.getTargetHost(), uri.getPath(), null);
		} else {
			return uri;
		}
	}
		
	@Override
	public void stop() {
		synchronized (startLock) {
			if(!isStarted) {
				return;
			}
			try {
				crawler.stop();
			} catch (InterruptedException e) {
				throw new RuntimeException("Attempt to stop proxy scan crawler was interrupted", e);
			}
			crawler = null;
			uriParser = null;
			isStarted = false;
		}
	}
	
	// Called holding startLock
	private void start() {
		if(isStarted) {
			return;
		}
		crawler = createWebCrawler();
		uriParser = createUriParser(crawler);
		crawler.start();
		isStarted = true;
		reloadModules();
	}
	
	private UriParser createUriParser(IWebCrawler crawler) {
		final IContentAnalyzer contentAnalyzer = scanner.getContentAnalyzerFactory().createContentAnalyzer(scanInstance);
		return new UriParser(config,
				basicModules,
				workspace,
				crawler,
				new UriFilter(config),
				contentAnalyzer,
				scanInstance,
				true);
	}
	
	private IWebCrawler createWebCrawler() {
		final IWebCrawler webCrawler = scanner.getWebCrawlerFactory().create(createRequestEngine(config));
		webCrawler.setStopOnEmptyQueue(false);
		return webCrawler;
	}

	private IHttpRequestEngine createRequestEngine(IScannerConfig config) {
		final IHttpRequestEngineFactory factory = scanner.getHttpRequestEngineFactory();
		final IHttpRequestEngineConfig requestEngineConfig = factory.createConfig();
		if (config.getCookieList() != null && !config.getCookieList().isEmpty()) {
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
		
		final IRequestOriginScanner requestOrigin = workspace.getRequestLog().getRequestOriginScanner(scanInstance);
		final IHttpRequestEngine requestEngine = factory.createRequestEngine(IHttpRequestEngine.EngineConfigType.CONFIG_SCANNER, requestEngineConfig, requestOrigin);
		HttpProtocolParams.setUserAgent(requestEngine.getHttpClient().getParams(), config.getUserAgent());
		requestEngine.setCookieStore(cookieStore);
		// REVISIT: consider moving authentication method to request engine config
		IIdentity identity = config.getScanIdentity();
		if (identity != null && identity.getAuthMethod() != null) {
			identity.getAuthMethod().setAuth(requestEngine);
		}

		return requestEngine;
	}

	@Override
	public List<IBasicModuleScript> getInjectionModules() {
		return Collections.unmodifiableList(basicModules);
	}
	
	@Override
	public void reloadModules() {
		final IScannerModuleRegistry moduleRegistry = scanner.getScannerModuleRegistry();
		if(basicModules == null) {
			basicModules = moduleRegistry.getBasicModules();
		} else {
			basicModules = moduleRegistry.updateBasicModules(basicModules);
		}
		if(uriParser != null) {
			uriParser.updateInjectionModules(basicModules);
		}
	}
}
