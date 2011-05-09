package com.subgraph.vega.ui.scanner.alerts;

import com.subgraph.vega.api.model.alerts.IScanAlert;

public interface IAlertTreeNode {
	void addAlert(IScanAlert alert);
	boolean hasChildren();
	int getAlertCount();
	Object[] getChildren();
	String getLabel();
	String getImage();
}
