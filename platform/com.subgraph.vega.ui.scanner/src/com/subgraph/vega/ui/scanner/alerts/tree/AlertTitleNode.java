package com.subgraph.vega.ui.scanner.alerts.tree;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.ui.scanner.alerts.IAlertTreeNode;

public class AlertTitleNode implements IAlertTreeNode {
	private final static String ALERT_ITEM = "icons/alert_item.png";

	private final String title;
	private final List<IScanAlert> alerts;
	
	AlertTitleNode(String title) {
		this.title = title;
		this.alerts = new ArrayList<IScanAlert>();
	}
	
	@Override
	public String getLabel() {
		return title;
	}
	
	@Override
	public void addAlert(IScanAlert alert) {
		alerts.add(alert);		
	}

	@Override
	public boolean hasChildren() {
		return alerts.size() > 0;
	}

	@Override
	public int getAlertCount() {
		return alerts.size();
	}

	@Override
	public Object[] getChildren() {
		return alerts.toArray();
	}

	@Override
	public String getImage() {
		return ALERT_ITEM;
	}
}
