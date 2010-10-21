package com.subgraph.vega.internal.crawler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class RequestConsumer implements Runnable {
	private final Logger logger = Logger.getLogger("crawler");
	private final IHttpRequestEngine requestEngine;
	private final BlockingQueue<CrawlerTask> crawlerRequestQueue;
	private final BlockingQueue<CrawlerTask> crawlerResponseQueue;
	private final CountDownLatch latch;
	private volatile boolean stop;
	
	RequestConsumer(IHttpRequestEngine requestEngine, BlockingQueue<CrawlerTask> requestQueue, BlockingQueue<CrawlerTask> responseQueue, CountDownLatch latch) {
		this.requestEngine = requestEngine;
		this.crawlerRequestQueue = requestQueue;
		this.crawlerResponseQueue = responseQueue;
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
			CrawlerTask task = crawlerRequestQueue.take();
			if(task.isExitTask()) {
				crawlerRequestQueue.add(task);
				return;
			}
			try {
				logger.info("Retrieving: " + task.getRequest().getURI());
				IHttpResponse response = requestEngine.sendRequest(task.getRequest());
				if(response == null) {
					logger.log(Level.WARNING, "No response was received for request to "+ task.getRequest().getURI());
				}
				task.setResponse(response);
				crawlerResponseQueue.put(task);
				
			} catch(ClientProtocolException e) {
				logger.log(Level.WARNING, "Protocol error processing request "+ task.getRequest().getURI(), e);
			} catch (IOException e) {
				logger.log(Level.WARNING, "IO error processing request "+ task.getRequest().getURI(), e);
			}
		}
	}

}
