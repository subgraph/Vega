package com.subgraph.vega.internal.crawler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpResponseProcessor implements Runnable {
	private final Logger logger = Logger.getLogger("crawler");
	private final WebCrawler crawler;
	private final BlockingQueue<CrawlerTask> crawlerRequestQueue;
	private final BlockingQueue<CrawlerTask> crawlerResponseQueue;
	private final CountDownLatch latch;
	private final TaskCounter counter;
	private volatile boolean stop;
	private volatile HttpUriRequest currentRequest;
	
	HttpResponseProcessor(WebCrawler crawler, BlockingQueue<CrawlerTask> requestQueue, BlockingQueue<CrawlerTask> responseQueue, CountDownLatch latch, TaskCounter counter) {
		this.crawler = crawler;
		this.crawlerRequestQueue = requestQueue;
		this.crawlerResponseQueue = responseQueue;
		this.latch = latch;
		this.counter = counter;
	}

	@Override
	public void run() {
		try {
			runLoop();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			synchronized(latch) {
				latch.countDown();
			}
		}
	}
	
	void stop() {
		stop = true;
		crawlerResponseQueue.offer(CrawlerTask.createExitTask());
		HttpUriRequest r = currentRequest;
		if(r != null)
			r.abort();
	}

	private void runLoop() throws InterruptedException {
		while(!stop) {	
			CrawlerTask task = (CrawlerTask)crawlerResponseQueue.take();
			if(task.isExitTask()) {
				crawlerRequestQueue.add(CrawlerTask.createExitTask());
				crawlerResponseQueue.add(task);
				return;
			}
			
			try {
				currentRequest = task.getRequest();
				if(task.getResponse() != null) {							
					task.getResponseProcessor().processResponse(crawler, currentRequest, task.getResponse(), task.getArgument());
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Unexpected exception processing crawler request: "+ currentRequest.getURI(), e);
			} finally {
				final HttpEntity entity = task.getResponse().getRawResponse().getEntity();
				if(entity != null)
					try {
						entity.consumeContent();
					} catch (IOException e) {
						logger.log(Level.WARNING, "I/O exception consuming request entity content for "+ currentRequest.getURI() + " : "+ e.getMessage());
					}
				currentRequest = null;
			}
			
			synchronized(counter) {
				counter.addCompletedTask();
				crawler.updateProgress();
			}

			if(task.finishTask()) {
				crawlerRequestQueue.add(CrawlerTask.createExitTask());
				crawlerResponseQueue.add(CrawlerTask.createExitTask());
				return;
			}
		}
	}
}
