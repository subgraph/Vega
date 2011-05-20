package com.subgraph.vega.internal.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerProgressTracker;
import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

public class WebCrawler implements IWebCrawler {
	private final static int REQUEST_THREAD_COUNT = 5;
	private final static int RESPONSE_THREAD_COUNT = 3;

	private final IHttpRequestEngine requestEngine;

	private final Executor executor = Executors.newFixedThreadPool(REQUEST_THREAD_COUNT + RESPONSE_THREAD_COUNT);
	private final BlockingQueue<CrawlerTask> requestQueue = new LinkedBlockingQueue<CrawlerTask>();
	private final BlockingQueue<CrawlerTask> responseQueue = new LinkedBlockingQueue<CrawlerTask>();
	private final List<RequestConsumer> requestConsumers = new ArrayList<RequestConsumer>(REQUEST_THREAD_COUNT);
	private final List<HttpResponseProcessor> responseProcessors = new ArrayList<HttpResponseProcessor>(RESPONSE_THREAD_COUNT);
	private final List<ICrawlerProgressTracker> eventHandlers;

	volatile private CountDownLatch latch;
	
	volatile private boolean crawlerRunning;
	
	private TaskCounter counter = new TaskCounter();
	
	WebCrawler(IHttpRequestEngine requestEngine) {
		this.requestEngine = requestEngine;
		this.eventHandlers = new ArrayList<ICrawlerProgressTracker>();
	}
	
	@Override
	public synchronized void start() {
		if(crawlerRunning)
			throw new IllegalStateException("Cannot call start() on running crawler instance");
	
		latch = new CountDownLatch(REQUEST_THREAD_COUNT + RESPONSE_THREAD_COUNT);
		
		updateProgress();
		
		for(int i = 0; i < RESPONSE_THREAD_COUNT; i++) {
			HttpResponseProcessor responseProcessor = new HttpResponseProcessor(this, requestQueue, responseQueue, latch, counter);
			responseProcessors.add(responseProcessor);
			executor.execute(responseProcessor);
		}
		
		for(int i = 0; i < REQUEST_THREAD_COUNT; i++) {
			RequestConsumer consumer = new RequestConsumer(requestEngine, requestQueue, responseQueue, latch);
			requestConsumers.add(consumer);
			executor.execute(consumer);
		}
		crawlerRunning = true;
	}
	
	public synchronized void stop() throws InterruptedException {
		for(HttpResponseProcessor responseProcessor: responseProcessors)
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

	@Override
	public void submitTask(HttpUriRequest request, ICrawlerResponseProcessor callback) {
		submitTask(request, callback, null);
	}
	
	@Override
	public void submitTask(HttpUriRequest request,
			ICrawlerResponseProcessor callback, Object argument) {
		CrawlerTask task = CrawlerTask.createTask(request, callback, argument);
		synchronized(counter) {
			counter.addNewTask();
			requestQueue.add(task);
		}
	}

	@Override
	public void registerProgressTracker(ICrawlerProgressTracker progress) {
		synchronized(counter) {
			eventHandlers.add(progress);
		}		
	}
	
	void updateProgress() {
		synchronized(counter) {
			for(ICrawlerProgressTracker pt: eventHandlers) 
				pt.progressUpdate(counter.getCompletedTasks(), counter.getTotalTasks());
		}
	}
}
