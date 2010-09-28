package com.subgraph.vega.internal.crawler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.urls.IUrlExtractor;

public class WebCrawler implements IWebCrawler {
	private final static int CRAWLER_THREAD_COUNT = 5;
	private final IWebModel model;
	private final IUrlExtractor urlExtractor;
	private final IHttpRequestEngine requestEngine;
	private final ICrawlerConfig config;

	private final Executor executor = Executors.newFixedThreadPool(CRAWLER_THREAD_COUNT + 1);
	private final BlockingQueue<CrawlerTask> requestQueue = new LinkedBlockingQueue<CrawlerTask>();
	private final BlockingQueue<CrawlerTask> responseQueue = new LinkedBlockingQueue<CrawlerTask>();
	private final List<RequestConsumer> requestConsumers = new ArrayList<RequestConsumer>(CRAWLER_THREAD_COUNT);
	private HttpResponseProcessor responseProcessor;
	volatile private CountDownLatch latch;
	
	volatile private boolean crawlerRunning;
	
	WebCrawler(IWebModel model, IUrlExtractor urlExtractor, IHttpRequestEngine requestEngine, ICrawlerConfig config) {
		this.model = model;
		this.urlExtractor = urlExtractor;
		this.requestEngine = requestEngine;
		this.config = config;		
	}
	
	@Override
	public synchronized void start() {
		if(crawlerRunning)
			throw new IllegalStateException("Cannot call start() on running crawler instance");
	
		latch = new CountDownLatch(CRAWLER_THREAD_COUNT + 1);
		
		for(URI uri: config.getInitialURIs())
			if(config.getURIFilter().filter(uri))
				requestQueue.add(CrawlerTask.createGetTask(uri));
			
		responseProcessor = new HttpResponseProcessor(requestQueue, responseQueue, model, urlExtractor, config, latch);
		
		executor.execute( responseProcessor );
		
		for(int i = 0; i < CRAWLER_THREAD_COUNT; i++) {
			RequestConsumer consumer = new RequestConsumer(requestEngine, requestQueue, responseQueue, latch);
			requestConsumers.add(consumer);
			executor.execute(consumer);
		}
		crawlerRunning = true;
	}
	
	public synchronized void stop() throws InterruptedException {
		responseProcessor.stop();
		for(RequestConsumer consumer: requestConsumers)
			consumer.stop();
		requestQueue.clear();
		requestQueue.put(CrawlerTask.createExitTask());
		responseQueue.clear();
		responseQueue.put(CrawlerTask.createExitTask());
		latch.await();
	}
	
	public void waitFinished() throws InterruptedException {
		latch.await();
	}

}
