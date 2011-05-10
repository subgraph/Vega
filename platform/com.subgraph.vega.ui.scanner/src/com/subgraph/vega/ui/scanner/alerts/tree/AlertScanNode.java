package com.subgraph.vega.ui.scanner.alerts.tree;

import org.apache.http.HttpHost;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanIdProvider;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class AlertScanNode extends AbstractAlertTreeNode implements IScanIdProvider {
	private final static String SCAN_IMAGE = "icons/scanner.png";
	private final static String PROXY_IMAGE = "icons/proxy.png";


	private final static String NO_HOSTNAME = "No Hostname";
	
	private final IWorkspace workspace;
	private final long scanId;
	
	private IScanInstance scanInstance;
	
	AlertScanNode(long scanId, IWorkspace workspace) {
		this.workspace = workspace;
		this.scanId = scanId;
	}

	void setScanInstance(IScanInstance scanInstance) {
		this.scanInstance = scanInstance;
	}

	public IScanInstance getScanInstance() {
		return scanInstance;
	}
	@Override
	public String getLabel() {
		if(scanId == -1) {
			return "Proxy  ";
		} else {
			return "Scan [id: #"+ Long.toString(scanId) +"]  ";
		}
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return new AlertHostNode(createKeyForAlert(alert));
	}
	@Override
	public String getImage() {
		if(scanId == -1) {
			return PROXY_IMAGE;
		} else {
			return SCAN_IMAGE;
		}
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		if(!alert.hasAssociatedRequest()) {
			return NO_HOSTNAME;
		}
		final IRequestLogRecord record = workspace.getRequestLog().lookupRecord(alert.getRequestId());
		if(record == null) {
			return NO_HOSTNAME;
		}
		final HttpHost host = record.getHttpHost();
		if(host == null) {
			return NO_HOSTNAME;
		}
		return host.toString();
	}

	@Override
	public long getScanId() {
		return scanId;
	}
}
