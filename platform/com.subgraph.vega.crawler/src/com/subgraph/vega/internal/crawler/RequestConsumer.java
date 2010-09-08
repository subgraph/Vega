package com.subgraph.vega.internal.crawler;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class RequestConsumer implements Runnable {
	private final HttpContext context;
	private final HttpClient httpClient;
	private final BlockingQueue<CrawlerTask> crawlerRequestQueue;
	private final BlockingQueue<CrawlerTask> crawlerResponseQueue;
	
	RequestConsumer(HttpClient httpClient, BlockingQueue<CrawlerTask> requestQueue, BlockingQueue<CrawlerTask> responseQueue) {
		this.context = new BasicHttpContext();
		this.httpClient = httpClient;
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
				HttpResponse response = httpClient.execute(task.getRequest(), context);
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
