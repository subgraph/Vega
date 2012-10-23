package com.subgraph.vega.ui.scanner.alerts;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.subgraph.vega.api.model.alerts.IScanAlertRepository;

public class ProxyPerspectiveFilter extends ViewerFilter {
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IAlertTreeNode) {
			IAlertTreeNode node = (IAlertTreeNode) element;
			return node.getScanInstance().getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID;
		}
		return false;
	}
}
