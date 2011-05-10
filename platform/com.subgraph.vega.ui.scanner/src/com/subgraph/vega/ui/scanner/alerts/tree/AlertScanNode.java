package com.subgraph.vega.ui.scanner.alerts.tree;

import org.apache.http.HttpHost;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class AlertScanNode extends AbstractAlertTreeNode {
	private final static String SCAN_IMAGE = "icons/scanner.png";

	private final static String NO_HOSTNAME = "No Hostname";
	
	private final IWorkspace workspace;
	private final long scanId;
	
	AlertScanNode(long scanId, IWorkspace workspace) {
		this.workspace = workspace;
		this.scanId = scanId;
	}

	@Override
	public String getLabel() {
		return "Scan [id: #"+ Long.toString(scanId) +"]  ";
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return new AlertHostNode(createKeyForAlert(alert));
	}
	@Override
	public String getImage() {
		return SCAN_IMAGE;
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		if(!alert.hasAssociatedRequest())
			return NO_HOSTNAME;
		final IRequestLogRecord record = workspace.getRequestLog().lookupRecord(alert.getRequestId());
		if(record == null)
			return NO_HOSTNAME;
		final HttpHost host = record.getHttpHost();
		if(host == null)
			return NO_HOSTNAME;
		return host.toString();
	}
}
