package com.subgraph.vega.ui.scanner.alerts.tree;

import com.subgraph.vega.api.model.alerts.IScanAlert;

public class AlertResourceNode extends AbstractAlertTreeNode {
	private final String resource;


	AlertResourceNode(String resource) {
		this.resource = resource;
	}

	@Override
	public String getLabel() {
		return resource;
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return null;
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		return null;
	}
	
	@Override
	public int getAlertCount() {
		return 1;
	}
}
