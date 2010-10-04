package com.subgraph.vega.impl.scanner;

import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.IScannerFactory;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.model.ScanModel;

public class ScannerFactory implements IScannerFactory {

	private ScanModel scanModel;
	
	private IWebCrawlerFactory crawlerFactory;
	private IHttpRequestEngine requestEngine;
	private IScannerModuleRegistry moduleRegistry;
	private IScanAlertRepository scanAlertRepository;
	
	protected void activate() {
		scanModel = new ScanModel(scanAlertRepository);
	}
	
	protected void deactivate() {
		
	}
	@Override
	public IScanModel getScanModel() {
		return scanModel;
	}

	@Override
	public IScannerConfig createScannerConfig() {
		return new ScannerConfig();
	}

	@Override
	public IScanner createScanner(IScannerConfig config) {
		return new Scanner(config, scanModel, crawlerFactory, requestEngine, moduleRegistry);
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
	
	protected void setScanAlertRepository(IScanAlertRepository repo) {
		this.scanAlertRepository = repo;
		
	}
	
	protected void unsetScanAlertRepository(IScanAlertRepository repo) {
		this.scanAlertRepository = null;
	}
}
