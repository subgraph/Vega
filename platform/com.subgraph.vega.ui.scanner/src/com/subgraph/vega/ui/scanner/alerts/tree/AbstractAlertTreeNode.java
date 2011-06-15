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

import java.util.HashMap;
import java.util.Map;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.ui.scanner.alerts.IAlertTreeNode;

abstract class AbstractAlertTreeNode implements IAlertTreeNode {
	
	protected final Map<String, IAlertTreeNode> nodeMap = new HashMap<String, IAlertTreeNode>();

	@Override
	public void addAlert(IScanAlert alert) {
		final IAlertTreeNode node = getNodeForAlert(alert);
		if(node != null) {
			node.addAlert(alert);
		}
	}

	@Override
	public boolean hasChildren() {
		return nodeMap.size() > 0;
	}
	
	@Override
	public int getAlertCount() {
		int n = 0;
		for(IAlertTreeNode node: nodeMap.values()) {
			n += node.getAlertCount();
		}
		return n;
	}
	
	
	@Override
	public synchronized IAlertTreeNode[] getChildren() {
		final IAlertTreeNode[] children = new IAlertTreeNode[nodeMap.size()];
		int i = 0;
		for(IAlertTreeNode node: nodeMap.values()) {
			children[i] = node;
			i += 1;
		}
		return children;
	}

	@Override
	public String getImage() {
		return null;
	}
	
	protected synchronized IAlertTreeNode getNodeForAlert(IScanAlert alert) {
		final String key = createKeyForAlert(alert);
		if(key == null) {
			return null;
		}
		if(!nodeMap.containsKey(key)) {
			nodeMap.put(key, createNodeForAlert(alert));
		}
		return nodeMap.get(key);
	}

	abstract protected IAlertTreeNode createNodeForAlert(IScanAlert alert);
	abstract protected String createKeyForAlert(IScanAlert alert);
}
