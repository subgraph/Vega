package com.subgraph.vega.internal.crawler;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class CrawlerTask {
	
	private static int outstandingTasks = 0;
	private static Object taskCountLock = new Object();
	
	static CrawlerTask createGetTask(URI uri) {
		synchronized (taskCountLock) {
			outstandingTasks++;
		}
		return new CrawlerTask(new HttpGet(uri), false);
	}
	
	static CrawlerTask createExitTask() {
		return new CrawlerTask(null, true);
	}
	
	private HttpUriRequest request;
	private HttpResponse response;
	private final boolean isExitTask;
	
	
	
	private CrawlerTask(HttpUriRequest request, boolean isExit) {
		this.request = request;
		this.isExitTask = isExit;
	}
	
	boolean isExitTask() {
		return isExitTask;
	}
	
	HttpUriRequest getRequest() {
		return request;
	}
	
	HttpResponse getResponse() {
		return response;
	}
	
	void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	boolean finishTask() {
		synchronized (taskCountLock) {
			outstandingTasks--;
			return (outstandingTasks == 0);
		}
	}
}
