package com.subgraph.vega.ui.scanner.alerts.tree;

import com.subgraph.vega.api.model.alerts.IScanAlert;

public class AlertHostNode extends AbstractAlertTreeNode {
	private final static String HOSTNAME_IMAGE = "icons/hostname.png";

	private final String hostname;
	
	AlertHostNode(String hostname) {
		this.hostname = hostname;
	}
	
	@Override
	public String getLabel() {
		return hostname;
	}

	@Override
	public String getImage() {
		return HOSTNAME_IMAGE;
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return new AlertSeverityNode(alert.getSeverity());
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		return alert.getSeverity().toString();
	}

}
