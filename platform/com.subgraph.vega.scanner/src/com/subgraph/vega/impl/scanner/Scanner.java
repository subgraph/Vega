package com.subgraph.vega.impl.scanner;

import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.events.CrawlerProgressEvent;
import com.subgraph.vega.impl.scanner.events.ScannerStatusChangeEvent;

public class Scanner implements IScanner {

	private IModel model;
	private ScannerStatus scannerStatus = ScannerStatus.SCAN_IDLE;
	private final EventListenerManager scanStatusListeners = new EventListenerManager();
	
	private IWebCrawlerFactory crawlerFactory;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IScannerModuleRegistry moduleRegistry;
	private ScannerTask scannerTask;
	private Thread scannerThread;
	
	protected void deactivate() {
		
	}
	@Override
	public IScanModel getScanModel() {
		return model.getCurrentWorkspace().getScanModel();
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
			if(newStatus == ScannerStatus.SCAN_COMPLETED || newStatus == ScannerStatus.SCAN_CANCELED)
				scannerTask = null;
		}
		scanStatusListeners.fireEvent(new ScannerStatusChangeEvent(newStatus));
	}
	
	void updateCrawlerProgress(int completed, int total) {
		scanStatusListeners.fireEvent(new CrawlerProgressEvent(completed, total));
	}
	
	@Override
	public synchronized void startScanner(IScannerConfig config) {
		if(scannerStatus != ScannerStatus.SCAN_IDLE && scannerStatus != ScannerStatus.SCAN_COMPLETED && scannerStatus != ScannerStatus.SCAN_CANCELED) 
			throw new IllegalStateException("Scanner is already running.  Verify scanner is not running with getScannerStatus() before trying to start.");
		
		if(config.getBaseURI() == null)
			throw new IllegalArgumentException("Cannot start scan because no baseURI was specified");
		
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
	
	protected void setModel(IModel model) {
		this.model = model;
	}
	
	protected void unsetModel(IModel model) {
		this.model = null;
	}
}
