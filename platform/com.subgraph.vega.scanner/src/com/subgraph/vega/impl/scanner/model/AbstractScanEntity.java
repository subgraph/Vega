package com.subgraph.vega.impl.scanner.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.subgraph.vega.api.scanner.model.IScanEntity;

public abstract class AbstractScanEntity implements IScanEntity {
	private final URI uri;
	private final Map<String, String> properties = new HashMap<String, String>();
	
	protected AbstractScanEntity(URI uri) {
		this.uri = uri;
	}
	
	public URI getURI() {
		return uri;
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}

}
