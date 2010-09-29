package com.subgraph.vega.api.crawler;

import java.net.URI;

public interface ICrawlerFilter {
	boolean filter(URI uri);
}
