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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.ui.scanner.alerts.IAlertTreeNode;

public class AlertTitleNode implements IAlertTreeNode {
	private final static String ALERT_ITEM = "icons/alert_item.png";
	private final AlertSeverityNode parentNode;
	private final String title;
	private final List<IScanAlert> alerts;
	
	AlertTitleNode(AlertSeverityNode parentNode, String title) {
		this.parentNode = parentNode;
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
	public String getKey() {
		return title;
	}
	
	@Override
	public void remove() {
		alerts.clear();
		parentNode.removeNode(getKey());
	}

	@Override
	public void addAlert(IScanAlert alert) {
		alerts.add(alert);		
	}

	@Override
	public void removeAlert(IScanAlert alert) {
		alerts.remove(alert);
	}

	@Override
	public Collection<IScanAlert> getAlerts() {
		return Collections.unmodifiableList(new ArrayList<IScanAlert>(alerts));
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

	@Override
	public IScanInstance getScanInstance() {
		return parentNode.getScanInstance();
	}
}
