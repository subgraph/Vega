package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.subgraph.vega.api.scanner.IScannerConfig;

public class UriFilter {
	private final IScannerConfig scannerConfig;
	private final Set<URI> visitedURIs = new HashSet<URI>();
	private final ArrayList<Pattern> exclusionList = new ArrayList<Pattern>();

	public UriFilter(IScannerConfig scannerConfig) {
		this.scannerConfig = scannerConfig;
		for (String exclusion: scannerConfig.getExclusions()) {
			exclusionList.add(Pattern.compile(exclusion));
		}
	}

	public boolean isExcluded(final URI uri) {
		for (Pattern p: exclusionList) {
			if (p.matcher(uri.toString()).find() == true) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAllowed(URI uri) {
		return uri.toString().startsWith(scannerConfig.getBaseURI().toString());
	}

	public synchronized boolean filter(URI uri) {
		if(visitedURIs.contains(uri) || !uri.toString().startsWith(scannerConfig.getBaseURI().toString()) || isExcluded(uri))
			return false;
		visitedURIs.add(uri);
		return true;
	}

}
