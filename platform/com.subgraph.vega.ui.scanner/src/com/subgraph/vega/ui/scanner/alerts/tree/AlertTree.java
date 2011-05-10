package com.subgraph.vega.ui.scanner.alerts.tree;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;

public class AlertTree extends AbstractAlertTreeNode {

	private final IWorkspace workspace;
	
	public AlertTree(IWorkspace workspace) {
		this.workspace = workspace;
	}
	
	public synchronized void addScan(IScanInstance scan) {
		final String key = Long.toString(scan.getScanId());
		if(!nodeMap.containsKey(key)) {
			nodeMap.put(key, new AlertScanNode(scan.getScanId(), workspace));
			return;
		}
		final AlertScanNode scanNode = (AlertScanNode) nodeMap.get(key);
		if(scanNode.getScanInstance() == null) {
			scanNode.setScanInstance(scan);
		}
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
