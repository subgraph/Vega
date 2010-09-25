package com.subgraph.vega.internal.crawler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

public class RequestConsumer implements Runnable {
	private final IHttpRequestEngine requestEngine;
	private final BlockingQueue<CrawlerTask> crawlerRequestQueue;
	private final BlockingQueue<CrawlerTask> crawlerResponseQueue;
	
	RequestConsumer(IHttpRequestEngine requestEngine, BlockingQueue<CrawlerTask> requestQueue, BlockingQueue<CrawlerTask> responseQueue) {
		this.requestEngine = requestEngine;
		this.crawlerRequestQueue = requestQueue;
		this.crawlerResponseQueue = responseQueue;
	}

	@Override
	public void run() {
		try {
			runLoop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	
	private void runLoop() throws InterruptedException {
		while(true) {
			CrawlerTask task = crawlerRequestQueue.take();
			if(task.isExitTask()) {
				crawlerRequestQueue.add(task);
				return;
			}
			try {
				HttpResponse response = requestEngine.sendRequest(task.getRequest());
				task.setResponse(response);
				crawlerResponseQueue.put(task);
			} catch(ClientProtocolException e) {
				e.printStackTrace();
				// fall through
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// fall through
			}
			if(task.finishTask()) {
				CrawlerTask exitTask = CrawlerTask.createExitTask();
				crawlerResponseQueue.add(exitTask);
			}
		}
	}

}
