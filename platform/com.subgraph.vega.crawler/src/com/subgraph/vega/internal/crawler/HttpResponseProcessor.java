package com.subgraph.vega.internal.crawler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.ICrawlerEventHandler;
import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.urls.IUrlExtractor;

public class HttpResponseProcessor implements Runnable {
	private final Logger logger = Logger.getLogger("crawler");
	private final BlockingQueue<CrawlerTask> crawlerRequestQueue;
	private final BlockingQueue<CrawlerTask> crawlerResponseQueue;
	private final IWebModel model;
	private final IUrlExtractor extractor;
	private final ICrawlerConfig config;
	private final CountDownLatch latch;
	private volatile boolean stop;
	
	HttpResponseProcessor(BlockingQueue<CrawlerTask> requestQueue, BlockingQueue<CrawlerTask> responseQueue, IWebModel model, IUrlExtractor extractor, ICrawlerConfig config, CountDownLatch latch) {
		this.crawlerRequestQueue = requestQueue;
		this.crawlerResponseQueue = responseQueue;
		this.model = model;
		this.extractor = extractor;
		this.config = config;
		this.latch = latch;
	}

	@Override
	public void run() {
		try {
			runLoop();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			latch.countDown();
		}
	}
	
	void stop() {
		stop = true;
	}

	private void runLoop() throws InterruptedException {
		while(!stop) {	
			CrawlerTask task = crawlerResponseQueue.take();
			if(task.isExitTask()) {
				crawlerRequestQueue.add(CrawlerTask.createExitTask());
				return;
			}
			
			try {
				processResponse(task.getResponse(), task.getRequest().getURI());
			} catch (IOException e) {
				logger.log(Level.WARNING, "IO error processing response from request: "+ task.getRequest().getURI(), e);
			}
			if(task.finishTask()) {
				crawlerRequestQueue.add(CrawlerTask.createExitTask());
				return;
			}	
		}
	}
	
	private void processResponse(HttpResponse response, URI page) throws IOException {
		HttpEntity entity = response.getEntity();
		if(entity == null)
			return;
		processEntity(entity, page);
		entity.consumeContent();
	}
	
	private void processEntity(HttpEntity entity, URI page) {
		Header contentType = entity.getContentType();
		String mimeType = (contentType == null) ? (null) : (contentType.getValue());
		IWebPath path = model.addURI(page);
		IWebGetTarget getTarget = path.addGetTarget(page.getQuery(), mimeType);
		getTarget.setVisited(true);
		if(mimeType != null && mimeType.startsWith("text/html")) {
			List<URI> uris = extractor.findUrls(entity, page);
			filterAndQueueURIs(uris);
		}
	}
	
	private void filterAndQueueURIs(List<URI> uris) {
		for(URI u: uris) {
			if(config.getURIFilter().filter(u)) {
				queueURI(u);
			}
		}
	}
	
	private void queueURI(URI uri) {
		for(ICrawlerEventHandler handler: config.getEventHandlers())
			handler.linkDiscovered(uri);
		crawlerRequestQueue.add(CrawlerTask.createGetTask(uri));
	}
}
