package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.crawler.ICrawlerProgressTracker;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;
import com.subgraph.vega.impl.scanner.urls.UriFilter;
import com.subgraph.vega.impl.scanner.urls.UriParser;

public class ScannerTask implements Runnable, ICrawlerProgressTracker {

	private final Logger logger = Logger.getLogger("scanner");
	private final IScanInstance scanInstance;
	private final Scanner scanner;
	private final IScannerConfig scannerConfig;
	private final IWorkspace workspace;
	private final IContentAnalyzer contentAnalyzer;
	private final IHttpRequestEngine requestEngine;
	
	private volatile boolean stopRequested;
	private IWebCrawler currentCrawler;
	
	ScannerTask(IScanInstance scanInstance, Scanner scanner, IScannerConfig config,  IHttpRequestEngine requestEngine, IWorkspace workspace, IContentAnalyzer contentAnalyzer) {
		this.scanInstance = scanInstance;
		this.scanner = scanner;
		this.scannerConfig = config;
		this.workspace = workspace;
		this.requestEngine = requestEngine;
		this.contentAnalyzer = contentAnalyzer;
		this.logger.setLevel(Level.ALL);
		scanner.getModuleRegistry().resetAllModuleTimestamps();
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
		contentAnalyzer.resetResponseProcessingQueue();
	}
	
	@Override
	public void run() {
		final List<IResponseProcessingModule> responseModules = scanner.getModuleRegistry().getResponseProcessingModules(true);
		contentAnalyzer.setResponseProcessingModules(responseModules);
		scanInstance.updateScanStatus(IScanInstance.SCAN_AUDITING);
		runCrawlerPhase();		
		if(stopRequested) {
			scanInstance.updateScanStatus(IScanInstance.SCAN_CANCELLED);
			logger.info("Scanner cancelled.");
		} else {
			scanInstance.updateScanStatus(IScanInstance.SCAN_COMPLETED);
			logger.info("Scanner completed");
		}
		workspace.getScanAlertRepository().setActiveScanInstance(null);
		workspace.unlock();
		printModuleRuntimeStats();
	}
	
	private void printModuleRuntimeStats() {
		logger.info("Scanning module runtime statistics:");
		for(IScannerModule m: scanner.getModuleRegistry().getAllModules(true)) {
			IScannerModuleRunningTime profile = m.getRunningTimeProfile();
			if(profile.getInvocationCount() > 0)
				logger.info(profile.toString());
		}
	}
	
	private void runCrawlerPhase() {
		logger.info("Starting crawling phase");
		currentCrawler = scanner.getCrawlerFactory().create(requestEngine);
		currentCrawler.registerProgressTracker(this);
		
		UriParser uriParser = new UriParser(scannerConfig, scanner.getModuleRegistry(), workspace, currentCrawler, new UriFilter(scannerConfig), contentAnalyzer, scanInstance);
		URI baseURI = scannerConfig.getBaseURI();
		uriParser.processUri(baseURI);
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
	public void progressUpdate(int completed, int total) {
		scanInstance.updateScanProgress(completed, total);
	}
}
