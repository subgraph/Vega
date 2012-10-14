package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
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
import com.subgraph.vega.api.scanner.ILiveScan;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.util.UriTools;
import com.subgraph.vega.impl.scanner.urls.UriFilter;
import com.subgraph.vega.impl.scanner.urls.UriParser;

public class LiveScan implements ILiveScan {
	
	private final IWorkspace workspace;
	private final Scanner scanner;
	private final IScanInstance scanInstance;
	private final IScannerConfig config;
	private  IWebCrawler crawler;
	private UriParser uriParser;
	private boolean isStarted = false;
	
	LiveScan(IWorkspace workspace, Scanner scanner) {
		this.workspace = workspace;
		this.scanner = scanner;
		this.scanInstance = workspace.getScanAlertRepository().getScanInstanceByScanId(IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID);
		this.config = new ScannerConfig();
		config.setDisplayDebugOutput(true);
		config.setLogAllRequests(true);
	}
	
	@Override
	public void scanGetTarget(URI target, List<NameValuePair> parameters) {
		if(!isStarted) {
			start();
		}
		final URI uri = UriTools.stripQueryFromUri(target);
		final IPathState ps = uriParser.processUri(uri);
		ps.maybeAddParameters(parameters);
	}

	@Override
	public void scanPostTarget(URI target, List<NameValuePair> parameters) {
		if(!isStarted) {
			start();
		}
		final URI uri = UriTools.stripQueryFromUri(target);
		final IPathState ps = uriParser.processUri(uri);
		ps.maybeAddPostParameters(parameters);
	}
		
	@Override
	public void stop() {
		if(!isStarted) {
			return;
		}
		try {
			crawler.stop();
		} catch (InterruptedException e) {
			throw new RuntimeException("Attempt to stop live scan crawler was interrupted", e);
		}
		crawler = null;
		uriParser = null;
		isStarted = false;
	}
	
	private void start() {
		if(isStarted) {
			return;
		}
		crawler = createWebCrawler();
		uriParser = createUriParser(crawler);
		crawler.start();
		isStarted = true;
	}
	
	private UriParser createUriParser(IWebCrawler crawler) {
		final IContentAnalyzer contentAnalyzer = scanner.getContentAnalyzerFactory().createContentAnalyzer(scanInstance);
		final List<IBasicModuleScript> basicModules = scanner.getScannerModuleRegistry().getBasicModules();
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
			final CookieStore cookieStore = requestEngineConfig.getCookieStore();
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
		
		final HttpClient client = factory.createUnencodingClient();
		HttpProtocolParams.setUserAgent(client.getParams(), config.getUserAgent());
		final IRequestOriginScanner requestOrigin = workspace.getRequestLog().getRequestOriginScanner(scanInstance);
		final IHttpRequestEngine requestEngine = factory.createRequestEngine(client, requestEngineConfig, requestOrigin);
		// REVISIT: consider moving authentication method to request engine config
		IIdentity identity = config.getScanIdentity();
		if (identity != null && identity.getAuthMethod() != null) {
			identity.getAuthMethod().setAuth(requestEngine);
		}

		return requestEngine;
	}
}
