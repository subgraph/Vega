package com.subgraph.vega.internal.crawler;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class UrlFilter {
	private final URI baseURI;
	private final Set<URI> visitedURLs = new HashSet<URI>();
	
	UrlFilter(URI baseURI) {
		this.baseURI = baseURI;
	}
	
	boolean filter(URI uri) {
		if(visitedURLs.contains(uri) || !uri.toString().startsWith(baseURI.toString()))
			return false;
		visitedURLs.add(uri);
		return true;
	}
}
