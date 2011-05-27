package com.subgraph.vega.ui.scanner.alerts.tree;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;

public class AlertTree extends AbstractAlertTreeNode {

	private final IWorkspace workspace;
	
	public AlertTree(IWorkspace workspace) {
		this.workspace = workspace;
	}
	
	public synchronized void addScan(IScanInstance scan) {
		if(scan.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
			if(scan.getAllAlerts().size() == 0) {
				return;
			}
		}
		final AlertScanNode scanNode = getScanNode(scan.getScanId());
		if(scanNode.getScanInstance() == null) {
			scanNode.setScanInstance(scan);
		}
	}
	
	public synchronized AlertScanNode getScanNode(long scanId) {
		final String key = Long.toString(scanId);
		if(!nodeMap.containsKey(key)) {
			final AlertScanNode scanNode = new AlertScanNode(scanId, workspace);
			if(scanId == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
				scanNode.setScanInstance(workspace.getScanAlertRepository().getProxyScanInstance());
			}
			nodeMap.put(key, scanNode);
		}
		return (AlertScanNode) nodeMap.get(key);
	}

	@Override
	public String getLabel() {
		return "[root]";
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return getScanNode(alert.getScanId());
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		return Long.toString(alert.getScanId());
	}
}
