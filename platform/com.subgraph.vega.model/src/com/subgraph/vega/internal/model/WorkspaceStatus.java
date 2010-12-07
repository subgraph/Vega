package com.subgraph.vega.internal.model;

public class WorkspaceStatus {
	private final ModelProperties properties;
	
	WorkspaceStatus() {
		properties = new ModelProperties();
	}
	
	ModelProperties getProperties() {
		return properties;
	}
}
