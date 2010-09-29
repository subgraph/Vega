package com.subgraph.vega.internal.crawler;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.subgraph.vega.api.crawler.ICrawlerFilter;

public class DefaultURIFilter implements ICrawlerFilter {
	private final URI baseURI;
	private final Set<URI> visitedURIs = new HashSet<URI>();

	DefaultURIFilter(URI baseURI) {
		this.baseURI = baseURI;
	}
	
	@Override
	public boolean filter(URI uri) {
		if(visitedURIs.contains(uri) || !uri.toString().startsWith(baseURI.toString()))
			return false;
		visitedURIs.add(uri);
		return true;
	}
}
