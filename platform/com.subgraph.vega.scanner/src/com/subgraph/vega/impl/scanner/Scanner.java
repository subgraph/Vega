package com.subgraph.vega.impl.scanner;

import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.model.ScanModel;

public class Scanner implements IScanner {
	
	private final IScannerConfig config;
	private final IWebCrawlerFactory crawlerFactory;
	private final ScanModel scanModel;
	private final IHttpRequestEngine requestEngine;
	private final IScannerModuleRegistry moduleRegistry;
	private ScannerTask scannerTask;
	private Thread scannerThread;
	
	Scanner(IScannerConfig config, ScanModel scanModel, IWebCrawlerFactory crawlerFactory, IHttpRequestEngine requestEngine, IScannerModuleRegistry moduleRegistry) {
		this.config = config;
		this.crawlerFactory = crawlerFactory;
		this.scanModel = scanModel;
		this.requestEngine = requestEngine;
		this.moduleRegistry = moduleRegistry;
	}
	
	IScannerConfig getConfig() {
		return config;
	}
	
	public void start() {
		moduleRegistry.refreshModuleScripts();
		scannerTask = new ScannerTask(config, scanModel, crawlerFactory, requestEngine, moduleRegistry);
		scannerThread = new Thread(scannerTask);
		scannerThread.start();
	}
}
