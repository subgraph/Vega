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
		if(alerts.size() == 1) {
			return title + " (" + alerts.get(0).getResource() + ")";
		}
		return title;
	}
	
	@Override
	public void addAlert(IScanAlert alert) {
		alerts.add(alert);		
	}

	@Override
	public boolean hasChildren() {
		return alerts.size() > 1;
	}

	@Override
	public int getAlertCount() {
		return alerts.size();
	}

	@Override
	public Object[] getChildren() {
		if(alerts.size() > 1) {
			return alerts.toArray();
		} else {
			return new Object[0];
		}
	}

	public IScanAlert getFirstAlert() {
		if(alerts.isEmpty()) {
			return null;
		} else {
			return alerts.get(0);
		}
	}
	@Override
	public String getImage() {
		return ALERT_ITEM;
	}
}
