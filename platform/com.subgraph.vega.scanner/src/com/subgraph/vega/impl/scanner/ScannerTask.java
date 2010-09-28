package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.logging.Logger;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.ICrawlerEventHandler;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanHost;
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.model.ScanModel;

public class ScannerTask implements Runnable, ICrawlerEventHandler {

	private final Logger logger = Logger.getLogger("scanner");
	private final IScannerConfig scannerConfig;
	private final ScanModel scanModel;
	private final IWebCrawlerFactory crawlerFactory;
	private final IHttpRequestEngine requestEngine;
	private final IScannerModuleRegistry moduleRegistry;
	
	ScannerTask(IScannerConfig config, ScanModel scanModel, IWebCrawlerFactory crawlerFactory, IHttpRequestEngine requestEngine, IScannerModuleRegistry moduleRegistry) {
		this.scannerConfig = config;
		this.scanModel = scanModel;
		this.crawlerFactory = crawlerFactory;
		this.requestEngine = requestEngine;
		this.moduleRegistry = moduleRegistry;
	}
	
	@Override
	public void run() {
		runCrawlerPhase();
		runPerHostModulePhase();
		runPerDirectoryModulePhase();
		logger.info("Scanner completed");
	}
	
	private void runCrawlerPhase() {
		logger.info("Starting crawling phase");
		ICrawlerConfig config = crawlerFactory.createBasicConfig(scannerConfig.getBaseURI());
		config.addEventHandler(this);
		IWebCrawler crawler = crawlerFactory.create(config);
		crawler.start();
		try {
			crawler.waitFinished();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Crawler finished");
	}
	
	@Override
	public void linkDiscovered(URI link) {
		scanModel.addDiscoveredURI(link);		
	}

	private void runPerHostModulePhase() {
		logger.info("Starting per host module phase");
		for(IScanHost host: scanModel.getUnscannedHosts()) {
			logger.info("Scanning host "+ host);
			for(IPerHostScannerModule m: moduleRegistry.getPerHostModules()) {
				m.runScan(host, requestEngine, scanModel);
			}
		}
	}
	
	private void runPerDirectoryModulePhase() {
		logger.info("Starting per directory module phase");
		for(IScanDirectory dir: scanModel.getUnscannedDirectories()) {
			logger.info("Scanning directory "+ dir);
			for(IPerDirectoryScannerModule m: moduleRegistry.getPerDirectoryModules()) {
				m.runScan(dir, requestEngine, scanModel);
			}
		}
	}
}
