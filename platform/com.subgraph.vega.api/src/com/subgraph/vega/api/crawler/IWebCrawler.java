package com.subgraph.vega.api.crawler;

import org.apache.http.client.methods.HttpUriRequest;

public interface IWebCrawler {
	void submitTask(HttpUriRequest request, ICrawlerResponseProcessor callback, Object argument);
	void submitTask(HttpUriRequest request, ICrawlerResponseProcessor callback);

	void registerProgressTracker(ICrawlerProgressTracker progress);
	void start();
	void stop() throws InterruptedException;
	void waitFinished() throws InterruptedException;
}
