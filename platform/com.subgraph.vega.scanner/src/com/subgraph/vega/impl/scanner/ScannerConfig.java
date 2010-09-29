package com.subgraph.vega.impl.scanner;

import java.net.URI;

import com.subgraph.vega.api.scanner.IScannerConfig;

public class ScannerConfig implements IScannerConfig {
	private URI baseURI;

	@Override
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;		
	}
	
	public URI getBaseURI() {
		return baseURI;
	}

}
