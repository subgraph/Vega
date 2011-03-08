package com.subgraph.vega.api.crawler;

public interface ICrawlerProgressTracker {
	void progressUpdate(int completed, int total);
}
