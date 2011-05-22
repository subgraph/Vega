package com.subgraph.vega.api.crawler;

import org.apache.http.client.methods.HttpUriRequest;

public interface ICrawlerProgressTracker {
	void progressUpdate(int completed, int total);
	void exceptionThrown(HttpUriRequest request, Throwable exception);
}
