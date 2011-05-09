package com.subgraph.vega.ui.scanner.alerts.tree;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;

public class AlertTree extends AbstractAlertTreeNode {

	private final IWorkspace workspace;
	
	public AlertTree(IWorkspace workspace) {
		this.workspace = workspace;
	}
	
	@Override
	public String getLabel() {
		return "[root]";
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return new AlertScanNode(alert.getScanId(), workspace);
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		return Long.toString(alert.getScanId());
	}
}
