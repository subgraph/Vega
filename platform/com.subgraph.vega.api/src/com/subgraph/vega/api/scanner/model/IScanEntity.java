package com.subgraph.vega.api.scanner.model;

import java.net.URI;

public interface IScanEntity {
	URI getURI();
	void setProperty(String key, String value);
	String getProperty(String key);

}
