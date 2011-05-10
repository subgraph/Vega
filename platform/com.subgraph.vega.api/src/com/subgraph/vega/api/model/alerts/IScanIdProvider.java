package com.subgraph.vega.api.model.alerts;

// Separated out so that the alert tree can publish a useful selection
public interface IScanIdProvider {
	long getScanId();
}
