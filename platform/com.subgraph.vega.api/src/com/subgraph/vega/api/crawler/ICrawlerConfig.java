package com.subgraph.vega.api.crawler;

import java.net.URI;
import java.util.List;

public interface ICrawlerConfig {
	void setFormParsingEnabled(boolean enabled);
	boolean isFormParsingEnabled();
	void addInitialURI(URI uri);
	void setURIFilter(ICrawlerFilter filter);
	void addEventHandler(ICrawlerEventHandler handler);
	URI getBaseURI();
	List<URI> getInitialURIs();
	ICrawlerFilter getURIFilter();
	List<ICrawlerEventHandler> getEventHandlers();
}
