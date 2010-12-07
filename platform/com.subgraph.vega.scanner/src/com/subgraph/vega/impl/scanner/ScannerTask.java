package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.ICrawlerEventHandler;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.IScanner.ScannerStatus;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;

public class ScannerTask implements Runnable, ICrawlerEventHandler {

	private final Logger logger = Logger.getLogger("scanner");
	private final Scanner scanner;
	private final IScannerConfig scannerConfig;
	private final IWorkspace workspace;


	private final IHttpRequestEngine requestEngine;
	
	private final IHttpResponseProcessor responseProcessor;
	private volatile boolean stopRequested;
	private IWebCrawler currentCrawler;
	
	ScannerTask(Scanner scanner, IScannerConfig config,  IHttpRequestEngine requestEngine, IWorkspace workspace) {
		this.scanner = scanner;
		this.scannerConfig = config;
		this.workspace = workspace;
		this.requestEngine = requestEngine;
		this.logger.setLevel(Level.ALL);
		responseProcessor = new ScannerResponseProcessor(scanner.getModuleRegistry().getResponseProcessingModules(), workspace);
		this.requestEngine.registerResponseProcessor(responseProcessor);
	}
	
	void stop() {
		stopRequested = true;
		if(currentCrawler != null)
			try {
				currentCrawler.stop();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	@Override
	public void run() {
		IWebPath basePath = workspace.getWebModel().getWebPathByUri(scannerConfig.getBaseURI());
		
		scanner.setScannerStatus(ScannerStatus.SCAN_CRAWLING);
		runCrawlerPhase();
		if(!stopRequested)
			scanner.setScannerStatus(ScannerStatus.SCAN_AUDITING);
		if(!stopRequested)
			runPerHostModulePhase();
		if(!stopRequested)
			runPerDirectoryModulePhase();
		if(stopRequested) {
			scanner.setScannerStatus(ScannerStatus.SCAN_CANCELED);
			logger.info("Scanner cancelled.");
		} else {
			scanner.setScannerStatus(ScannerStatus.SCAN_COMPLETED);
			logger.info("Scanner completed");
		}
		workspace.unlock();
	}
	
	private void runCrawlerPhase() {
		logger.info("Starting crawling phase");
		ICrawlerConfig config = scanner.getCrawlerFactory().createBasicConfig(scannerConfig.getBaseURI());
		config.addEventHandler(this);
		currentCrawler = scanner.getCrawlerFactory().create(config, requestEngine);
		currentCrawler.start();
		try {
			currentCrawler.waitFinished();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentCrawler = null;
		logger.info("Crawler finished");
	}
	
	@Override
	public void linkDiscovered(URI link) {
		workspace.getWebModel().getWebPathByUri(link);
	}

	private void runPerHostModulePhase() {
		logger.info("Starting per host module phase");

		final IWebModel webModel = workspace.getWebModel();
		for(IWebHost host: webModel.getUnscannedHosts()) {
			for(IPerHostScannerModule m: scanner.getModuleRegistry().getPerHostModules()) {
				if(stopRequested)
					return;
				m.runScan(host, requestEngine, workspace);
			}
			host.setScanned();
		}
	}
	
	private void runPerDirectoryModulePhase() {
		logger.info("Starting per directory module phase");
		final IWebModel webModel = workspace.getWebModel();
		for(IWebPath path: webModel.getUnscannedPaths()) {
			for(IPerDirectoryScannerModule m: scanner.getModuleRegistry().getPerDirectoryModules()) {
				if(stopRequested)
					return;
				m.runScan(path, requestEngine, workspace);
				
			}
			path.setScanned();
		}
	}

	@Override
	public void progressUpdate(int completed, int total) {
		scanner.updateCrawlerProgress(completed, total);		
	}

	@Override
	public void responseProcessed(URI uri) {
		// XXX This is not currently called by crawler
	}
}
