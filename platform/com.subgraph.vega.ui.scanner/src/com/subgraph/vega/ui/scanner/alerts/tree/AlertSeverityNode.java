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

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlert.Severity;
import com.subgraph.vega.ui.scanner.alerts.IAlertTreeNode;

public class AlertSeverityNode extends AbstractAlertTreeNode {
	private final static String ALERT_LOW = "icons/alert_low.png";
	private final static String ALERT_MEDIUM = "icons/alert_medium.png";
	private final static String ALERT_HIGH = "icons/alert_high.png";
	private final static String ALERT_INFO = "icons/alert_info.png";
	
	private final Severity severity;
		
	AlertSeverityNode(Severity severity) {
		this.severity = severity;
	}

	public int getSeverityIndex() {
		switch(severity) {
		case HIGH:
			return 5;
		case MEDIUM:
			return 4;
		case LOW:
			return 3;
		case INFO:
			return 2;
		default:
			return 1;
		}
	}

	@Override
	public String getLabel() {
		switch(severity) {
		case HIGH:
			return "High";
		case MEDIUM:
			return "Medium";
		case LOW:
			return "Low";
		case INFO:
			return "Info";
		}
		return "";
	}
	
	@Override
	public String getImage() {
		switch(severity) {
		case HIGH:
			return ALERT_HIGH;
		case MEDIUM:
			return ALERT_MEDIUM;
		case LOW:
			return ALERT_LOW;
		case INFO:
			return ALERT_INFO;
		}
		return null;
	}

	@Override
	protected IAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return new AlertTitleNode(alert.getTitle());
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		return alert.getTitle();
	}

}
