package com.subgraph.vega.api.crawler;

import java.net.URI;

public interface ICrawlerEventHandler {
	void linkDiscovered(URI link);
	void progressUpdate(int completed, int total);
}
