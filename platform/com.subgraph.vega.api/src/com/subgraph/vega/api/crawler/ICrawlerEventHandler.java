package com.subgraph.vega.api.crawler;

import java.net.URI;

public interface ICrawlerEventHandler {
	void responseProcessed(URI uri);
	void linkDiscovered(URI link);
	void progressUpdate(int completed, int total);
}
