package com.subgraph.vega.internal.crawler;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class CrawlerTask {
	
	private static int outstandingTasks = 0;
	private static Object taskCountLock = new Object();
	
	static CrawlerTask createTask(HttpUriRequest request, ICrawlerResponseProcessor responseProcessor, Object argument) {
		synchronized(taskCountLock) {
			outstandingTasks++;
		}
		return new CrawlerTask(request, responseProcessor, argument, false);
	}
	
	static CrawlerTask createExitTask() {
		return new CrawlerTask(null, null, null, true);
	}
	
	private final HttpUriRequest request;
	private final ICrawlerResponseProcessor responseProcessor;
	private final Object argument;
	private IHttpResponse response;
	private Throwable exception;
	private final boolean isExitTask;
	
	
	
	private CrawlerTask(HttpUriRequest request, ICrawlerResponseProcessor responseProcessor, Object argument, boolean isExit) {
		this.request = request;
		this.responseProcessor = responseProcessor;
		this.argument = argument;
		this.isExitTask = isExit;
	}
	
	boolean isExitTask() {
		return isExitTask;
	}
	
	public HttpUriRequest getRequest() {
		return request;
	}
	
	public Object getArgument() {
		return argument;
	}
	
	IHttpResponse getResponse() {
		return response;
	}
	
	void setResponse(IHttpResponse response) {
		this.response = response;
	}
	
	void setException(Throwable exception) {
		this.exception = exception;
	}
	
	Throwable getException() {
		return exception;
	}
	
	boolean causedException() {
		return exception != null;
	}

	boolean finishTask() {
		synchronized (taskCountLock) {
			outstandingTasks--;
			return (outstandingTasks == 0);
		}
	}

	public ICrawlerResponseProcessor getResponseProcessor() {
		return responseProcessor;
	}
}
