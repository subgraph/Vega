package com.subgraph.vega.internal.crawler;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;

public class RequestConsumer implements Runnable {
	private final Logger logger = Logger.getLogger("crawler");
	private final IHttpRequestEngine requestEngine;
	private final BlockingQueue<CrawlerTask> crawlerRequestQueue;
	private final BlockingQueue<CrawlerTask> crawlerResponseQueue;
	private final CountDownLatch latch;
	private final IWorkspace workspace;
	private volatile HttpUriRequest currentRequest;
	private volatile boolean stop;
	
	RequestConsumer(IHttpRequestEngine requestEngine, BlockingQueue<CrawlerTask> requestQueue, BlockingQueue<CrawlerTask> responseQueue, CountDownLatch latch, IWorkspace workspace) {
		this.requestEngine = requestEngine;
		this.crawlerRequestQueue = requestQueue;
		this.crawlerResponseQueue = responseQueue;
		this.latch = latch;
		this.workspace = workspace;
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
				if(latch.getCount() == 0)
					workspace.unlock();
			}
		}		
	}
	
	void stop() {
		HttpUriRequest r = currentRequest;
		stop = true;
		if(r != null)
			r.abort();
	}
	
	private void runLoop() throws InterruptedException {
		while(!stop) {
			CrawlerTask task = crawlerRequestQueue.take();
			
			if(task.isExitTask()) {
				// Put poison pill back in queue so every other RequestConsumer task will see it.
				crawlerRequestQueue.add(task);
				return;
			}
			
			logger.info("Retrieving: " + task.getRequest().getURI());
			IHttpResponse response = sendRequest(task.getRequest());
			if(response == null) {
				logger.log(Level.WARNING, "No response was received for request to "+ task.getRequest().getURI());
			}
			task.setResponse(response);
			crawlerResponseQueue.put(task);	
		}
	}
	
	private IHttpResponse sendRequest(HttpUriRequest request) {		
		try {
			currentRequest = request;
			return requestEngine.sendRequest(request);
		} catch (InterruptedIOException e) {
			stop = true;
			return null;
		} catch (ClientProtocolException e) {
			logger.log(Level.WARNING, "Protocol error processing request "+ request.getURI(), e);
			return null;
		} catch (IOException e) {
			logger.log(Level.WARNING, "IO error processing request "+ request.getURI(), e);
			return null;
		} finally {
			currentRequest = null;
		}
	}

}
