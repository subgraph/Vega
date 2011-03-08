package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerProgressTracker;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebMountPoint;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.IScanner.ScannerStatus;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerMountPointModule;
import com.subgraph.vega.api.scanner.modules.IPerResourceScannerModule;
import com.subgraph.vega.impl.scanner.urls.UriFilter;
import com.subgraph.vega.impl.scanner.urls.UriParser;
import com.subgraph.vega.urls.IUrlExtractor;

public class ScannerTask implements Runnable, ICrawlerProgressTracker {

	private final Logger logger = Logger.getLogger("scanner");
	private final Scanner scanner;
	private final IScannerConfig scannerConfig;
	private final IWorkspace workspace;
	private final IUrlExtractor urlExtractor;
	


	private final IHttpRequestEngine requestEngine;
	
	private final IHttpResponseProcessor responseProcessor;
	private volatile boolean stopRequested;
	private IWebCrawler currentCrawler;
	
	ScannerTask(Scanner scanner, IScannerConfig config,  IHttpRequestEngine requestEngine, IWorkspace workspace, IUrlExtractor urlExtractor) {
		this.scanner = scanner;
		this.scannerConfig = config;
		this.workspace = workspace;
		this.urlExtractor = urlExtractor;
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
		workspace.lock();
		
		scanner.setScannerStatus(ScannerStatus.SCAN_CRAWLING);
		runCrawlerPhase();
		if(!stopRequested)
			scanner.setScannerStatus(ScannerStatus.SCAN_AUDITING);
		if(!stopRequested)
			runPerHostModulePhase();
		if(!stopRequested)
			runPerDirectoryModulePhase();
		if(!stopRequested)
			runPerResourceModulePhase();
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
		currentCrawler = scanner.getCrawlerFactory().create(requestEngine);
		currentCrawler.registerProgressTracker(this);
		
		UriParser uriParser = new UriParser(workspace, currentCrawler, new UriFilter(scannerConfig.getBaseURI()), urlExtractor);
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

	private void runPerResourceModulePhase() {
		logger.info("Starting per resource module phase");
		final IWebModel webModel = workspace.getWebModel();
		for(IWebPath path: webModel.getAllPaths()) {
			for(IPerResourceScannerModule m: scanner.getModuleRegistry().getPerResourceModules()) {
				if(stopRequested)
					return;
				m.runModule(path, requestEngine, workspace);
			}
		}
	}
	@Override
	public void progressUpdate(int completed, int total) {
		scanner.updateCrawlerProgress(completed, total);		
	}
}
