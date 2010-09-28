package com.subgraph.vega.internal.crawler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.ICrawlerEventHandler;
import com.subgraph.vega.api.crawler.ICrawlerFilter;

public class BasicCrawlerConfig implements ICrawlerConfig {
	
	private final URI baseURI;
	private final Set<URI> initialURIs;
	private final List<ICrawlerEventHandler> eventHandlers;
	private ICrawlerFilter filter;
	
	BasicCrawlerConfig(URI baseURI) {
		this.baseURI = baseURI;
		this.initialURIs = new LinkedHashSet<URI>();
		initialURIs.add(baseURI);
		eventHandlers = new ArrayList<ICrawlerEventHandler>();
		filter = new DefaultURIFilter(baseURI);
	}
	
	@Override
	public URI getBaseURI() {
		return baseURI;
	}

	@Override
	public List<URI> getInitialURIs() {
		return new ArrayList<URI>(initialURIs);
	}

	@Override
	public ICrawlerFilter getURIFilter() {
		return filter;
	}

	@Override
	public void addInitialURI(URI uri) {
		initialURIs.add(uri);		
	}

	@Override
	public void setURIFilter(ICrawlerFilter filter) {
		this.filter = filter;		
	}

	@Override
	public void addEventHandler(ICrawlerEventHandler handler) {
		eventHandlers.add(handler);		
	}

	@Override
	public List<ICrawlerEventHandler> getEventHandlers() {
		return Collections.unmodifiableList(eventHandlers);
	}
	
	

}
