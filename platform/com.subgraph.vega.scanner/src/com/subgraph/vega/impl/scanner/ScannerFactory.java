package com.subgraph.vega.impl.scanner;

import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.IScannerFactory;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

public class ScannerFactory implements IScannerFactory {

	private IWebCrawlerFactory crawlerFactory;
	private IHttpRequestEngine requestEngine;
	private IScannerModuleRegistry moduleRegistry;
	
	@Override
	public IScannerConfig createScannerConfig() {
		return new ScannerConfig();
	}

	@Override
	public IScanner createScanner(IScannerConfig config) {
		return new Scanner(config, crawlerFactory, requestEngine, moduleRegistry);
	}

	protected void setCrawlerFactory(IWebCrawlerFactory crawlerFactory) {
		this.crawlerFactory = crawlerFactory;
	}
	
	protected void unsetCrawlerFactory(IWebCrawlerFactory crawlerFactory) {
		this.crawlerFactory = null;
	}
	
	protected void setRequestEngine(IHttpRequestEngine requestEngine) {
		this.requestEngine = requestEngine;
	}
	
	protected void unsetRequestEngine(IHttpRequestEngine requestEngine) {
		this.requestEngine = null;
	}
	
	protected void setModuleRegistry(IScannerModuleRegistry registry) {
		this.moduleRegistry = registry;
	}
	
	protected void unsetModuleRegistry(IScannerModuleRegistry registry) {
		this.moduleRegistry = null;
	}
}
