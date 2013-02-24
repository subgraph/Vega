package com.subgraph.vega.ui.scanner.alerts;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.util.UriTools;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertHostNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertSeverityNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTitleNode;

public class CurrentScopeFilter extends ViewerFilter {

	private final IWorkspace workspace;
	private final ITargetScope currentScope;
	
	public CurrentScopeFilter(IWorkspace workspace, ITargetScope currentScope) {
		this.workspace = workspace;
		this.currentScope = currentScope;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return selectElement(element);
	}
	
	private boolean selectElement(Object element) {
		if(element instanceof AlertScanNode) {
			return selectAlertScanNode((AlertScanNode) element);
		} else if(element instanceof AlertHostNode) {
			return selectAlertHostNode((AlertHostNode) element);
		} else if(element instanceof AlertSeverityNode) {
			return selectAlertSeverityNode((AlertSeverityNode) element);
		} else if(element instanceof AlertTitleNode) {
			return selectAlertTitleNode((AlertTitleNode) element);
		} else if(element instanceof IScanAlert) {
			return selectScanAlert((IScanAlert) element);
		} else {
			return false;
		}
	}
	
	private boolean selectAlertScanNode(AlertScanNode node) {
		for(IAlertTreeNode n: node.getChildren()) {
			if(selectElement(n)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean selectAlertHostNode(AlertHostNode node) {
		for(IAlertTreeNode n: node.getChildren()) {
			if(selectElement(n)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean selectAlertSeverityNode(AlertSeverityNode node) {
		for(IAlertTreeNode n: node.getChildren()) {
			if(selectElement(n)) {
				return true;
			}
		}
		return false;	
	}
	
	private boolean selectAlertTitleNode(AlertTitleNode node) {
		if(node.hasChildren()) {
			for(Object ob: node.getChildren()) {
				if(selectElement(ob)) {
					return true;
				}
			}
			return false;
		}
		return selectScanAlert(node.getFirstAlert());
	}
	
	private boolean selectScanAlert(IScanAlert alert) {
		final IRequestLogRecord record = alertToRecord(alert);
		if(record == null) {
			return false;
		}
		final HttpHost host = record.getHttpHost();
		final String uriPath = recordToUriPath(record);
		if(host == null || uriPath == null) {
			return false;
		}
		return currentScope.filter(host, uriPath);
	}

	private IRequestLogRecord alertToRecord(IScanAlert alert) {
		if(alert == null) {
			return null;
		}
		return workspace.getRequestLog().lookupRecord(alert.getRequestId());
	}

	private String recordToUriPath(IRequestLogRecord record) {
		final HttpRequest request = record.getRequest();
		if(request == null) {
			return null;
		}
		return UriTools.removeUnicodeEscapes(request.getRequestLine().getUri());
	}
}
