/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerProgressTracker;
import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

public class WebCrawler implements IWebCrawler {
	private final IHttpRequestEngine requestEngine;
	private final Executor executor;
	private final BlockingQueue<CrawlerTask> requestQueue = new LinkedBlockingQueue<CrawlerTask>();
	private final BlockingQueue<CrawlerTask> responseQueue = new LinkedBlockingQueue<CrawlerTask>();
	private final List<RequestConsumer> requestConsumers;
	private final List<HttpResponseProcessor> responseProcessors;
	private final List<ICrawlerProgressTracker> eventHandlers;
	private final int requestThreadCount;
	private final int responseThreadCount;

	volatile private CountDownLatch latch;
	
	volatile private boolean crawlerRunning;
	
	private TaskCounter counter = new TaskCounter();
	private AtomicInteger outstandingTasks = new AtomicInteger();
	
	WebCrawler(IHttpRequestEngine requestEngine, int requestThreadCount, int responseThreadCount) {
		this.requestEngine = requestEngine;
		this.requestThreadCount = requestThreadCount;
		this.responseThreadCount = responseThreadCount;
		this.executor = Executors.newFixedThreadPool(requestThreadCount + responseThreadCount);
		this.requestConsumers = new ArrayList<RequestConsumer>(requestThreadCount);
		this.responseProcessors = new ArrayList<HttpResponseProcessor>(responseThreadCount);
		this.eventHandlers = new ArrayList<ICrawlerProgressTracker>();
	}
	
	@Override
	public synchronized void start() {
		if(crawlerRunning)
			throw new IllegalStateException("Cannot call start() on running crawler instance");
	
		latch = new CountDownLatch(requestThreadCount + responseThreadCount);
		
		updateProgress();
		
		for(int i = 0; i < responseThreadCount; i++) {
			HttpResponseProcessor responseProcessor = new HttpResponseProcessor(this, requestQueue, responseQueue, latch, counter, outstandingTasks);
			responseProcessors.add(responseProcessor);
			executor.execute(responseProcessor);
		}
		
		for(int i = 0; i < requestThreadCount; i++) {
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
		outstandingTasks.incrementAndGet();
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
	
	void notifyException(HttpUriRequest request, Throwable exception) {
		for(ICrawlerProgressTracker pt: eventHandlers) {
			pt.exceptionThrown(request, exception);
		}
	}
}
