/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.scanner.alerts.tree;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.ui.scanner.alerts.IAlertTreeNode;

public class AlertTree extends AbstractAlertTreeNode {

	private final IWorkspace workspace;
	
	public AlertTree(IWorkspace workspace) {
		super(null);
		this.workspace = workspace;
	}
	
	public synchronized AlertScanNode addScan(IScanInstance scan) {
		if(scan.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
			if(scan.getAllAlerts().size() == 0) {
				return null;
			}
		}
		final AlertScanNode scanNode = getScanNode(scan.getScanId());
		if(scanNode.getScanInstance() == null) {
			scanNode.setScanInstance(scan);
		}
		return scanNode;
	}
	
	public synchronized AlertScanNode getScanNode(long scanId) {
		final String key = Long.toString(scanId);
		AlertScanNode node = (AlertScanNode) nodeMap.get(key);
		if (node == null) {
			node = new AlertScanNode(this, scanId, workspace);
			if(scanId == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
				node.setScanInstance(workspace.getScanAlertRepository().getProxyScanInstance());
			}
			nodeMap.put(key, node);
		}
		return node;
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
