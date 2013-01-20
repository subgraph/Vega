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

public class AlertHostNode extends AbstractAlertTreeNode {
	private final static String HOSTNAME_IMAGE = "icons/hostname.png";
	private final String hostname;

	AlertHostNode(AbstractAlertTreeNode parentNode, String hostname) {
		super(parentNode);
		this.hostname = hostname;
	}

	public String getKey() {
		return hostname;
	}

	@Override
	public String getLabel() {
		return hostname;
	}

	@Override
	public String getImage() {
		return HOSTNAME_IMAGE;
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return new AlertSeverityNode(this, alert.getSeverity());
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		return alert.getSeverity().toString();
	}
}
