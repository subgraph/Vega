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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.ui.scanner.alerts.IAlertTreeNode;

abstract class AbstractAlertTreeNode implements IAlertTreeNode {
	protected final AbstractAlertTreeNode parentNode;
	protected final Map<String, IAlertTreeNode> nodeMap = new HashMap<String, IAlertTreeNode>();

	protected AbstractAlertTreeNode(AbstractAlertTreeNode parentNode) {
		this.parentNode = parentNode;
	}

	@Override
	public void addAlert(IScanAlert alert) {
		final IAlertTreeNode node = getNodeForAlert(alert);
		if(node != null) {
			node.addAlert(alert);
		}
	}
	
	@Override
	public void removeAlert(IScanAlert alert) {
		final String key = createKeyForAlert(alert);
		for(IAlertTreeNode node: getChildren()) {
			if(node.getKey().equals(key)) {
				node.removeAlert(alert);
			}
			if(node.getAlertCount() == 0) {
				removeNode(key);
			}
		}
	}

	@Override
	public void remove() {
		for(IAlertTreeNode node: getChildren()) {
			node.remove();
		}
		parentNode.removeNode(getKey());
	}
	
	@Override
	public Collection<IScanAlert> getAlerts() {
		final List<IScanAlert> alerts = new ArrayList<IScanAlert>();
		for(IAlertTreeNode node: getChildren()) {
			alerts.addAll(node.getAlerts());
		}
		return Collections.unmodifiableList(alerts);
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
		return nodeMap.values().toArray(new IAlertTreeNode[0]);
	}

	public synchronized void removeNode(String key) {
		nodeMap.remove(key);
	}

	@Override
	public String getImage() {
		return null;
	}

	@Override
	public IScanInstance getScanInstance() {
		if (parentNode != null) {
			return parentNode.getScanInstance();
		}
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
