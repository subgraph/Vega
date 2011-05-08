package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.subgraph.vega.api.scanner.IScannerConfig;

public class UriFilter {
	
	private final IScannerConfig scannerConfig;
	private final Set<URI> visitedURIs = new HashSet<URI>();
	
	public UriFilter(IScannerConfig scannerConfig) {
		this.scannerConfig = scannerConfig;
	}
	
	public boolean isAllowed(URI uri) {
		return uri.toString().startsWith(scannerConfig.getBaseURI().toString());
	}

	public synchronized boolean filter(URI uri) {
		if(visitedURIs.contains(uri) || !uri.toString().startsWith(scannerConfig.getBaseURI().toString()))
			return false;
		visitedURIs.add(uri);
		return true;
	}

}
