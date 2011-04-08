package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class UriFilter {
	
	private final URI baseURI;
	private final Set<URI> visitedURIs = new HashSet<URI>();
	
	public UriFilter(URI baseURI) {
		this.baseURI = baseURI;
	}
	
	public boolean isAllowed(URI uri) {
		return uri.toString().startsWith(baseURI.toString());
	}

	public synchronized boolean filter(URI uri) {
		if(visitedURIs.contains(uri) || !uri.toString().startsWith(baseURI.toString()))
			return false;
		visitedURIs.add(uri);
		return true;
	}

}
