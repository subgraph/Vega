package com.subgraph.vega.impl.scanner;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.events.ScannerStatusChangeEvent;
import com.subgraph.vega.impl.scanner.model.ScanModel;

public class Scanner implements IScanner {

	private ScanModel scanModel;
	private ScannerStatus scannerStatus = ScannerStatus.SCAN_IDLE;
	private final EventListenerManager scanStatusListeners = new EventListenerManager();
	
	private IWebCrawlerFactory crawlerFactory;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IScannerModuleRegistry moduleRegistry;
	private ScannerTask scannerTask;
	private Thread scannerThread;
	private IScanAlertRepository scanAlertRepository;
	private IHTMLParser htmlParser;
	private IConsole console;
	
	protected void activate() {
		scanModel = new ScanModel(scanAlertRepository, htmlParser, console);
		moduleRegistry.setScanModel(scanModel);
	}
	
	protected void deactivate() {
		
	}
	@Override
	public ScanModel getScanModel() {
		return scanModel;
	}

	IWebCrawlerFactory getCrawlerFactory() {
		return crawlerFactory;
	}
	
	IScannerModuleRegistry getModuleRegistry() {
		return moduleRegistry;
	}
	
	void fireStatusChangeEvent(IEvent event) {
		scanStatusListeners.fireEvent(event);
	}
	
	@Override
	public IScannerConfig createScannerConfig() {
		return new ScannerConfig();
	}
	
	@Override
	public synchronized ScannerStatus getScannerStatus() {
		return scannerStatus;
	}

	void setScannerStatus(ScannerStatus newStatus) {
		synchronized(this) {
			this.scannerStatus = newStatus;
			if(newStatus == ScannerStatus.SCAN_COMPLETED)
				scannerTask = null;
		}
		scanStatusListeners.fireEvent(new ScannerStatusChangeEvent(newStatus));
	}
	
	@Override
	public synchronized void startScanner(IScannerConfig config) {
		if(scannerStatus != ScannerStatus.SCAN_IDLE && scannerStatus != ScannerStatus.SCAN_COMPLETED) 
			throw new IllegalStateException("Scanner is already running.  Verify scanner is not running with getScannerStatus() before trying to start.");
		
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		moduleRegistry.refreshModuleScripts();
		scannerTask = new ScannerTask(this, config, requestEngine);
		scannerThread = new Thread(scannerTask);
		setScannerStatus(ScannerStatus.SCAN_STARTING);

		scannerThread.start();
	}
	
	@Override
	public void stopScanner() {
		if(scannerTask != null)
			scannerTask.stop();
	}

	@Override
	public void registerScannerStatusChangeListener(IEventHandler listener) {
		scanStatusListeners.addListener(listener);		
	}
	
	@Override
	public void runDomTests() {
		moduleRegistry.runDomTests();
	}
	
	protected void setCrawlerFactory(IWebCrawlerFactory crawlerFactory) {
		this.crawlerFactory = crawlerFactory;
	}
	
	protected void unsetCrawlerFactory(IWebCrawlerFactory crawlerFactory) {
		this.crawlerFactory = null;
	}
	
	protected void setRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = factory;
	}
	
	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
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
	
	protected void setHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = htmlParser;
	}
	
	protected void unsetHTMLParser(IHTMLParser htmlParser) {
		this.htmlParser = null;
	}
	
	protected void setConsole(IConsole console) {
		this.console = console;
	}
	
	protected void unsetConsole(IConsole console) {
		this.console = null;
	}
}
