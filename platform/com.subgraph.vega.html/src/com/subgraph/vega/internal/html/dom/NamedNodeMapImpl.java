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
package com.subgraph.vega.internal.html.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl implements NamedNodeMap {

	private final Map<String, NodeImpl> nodeMap = new HashMap<String, NodeImpl>();
	private final List<NodeImpl> indexedNodes = new ArrayList<NodeImpl>();
	

	void addNode(String name, NodeImpl node) {
		if(nodeMap.containsKey(name))
			return;
		nodeMap.put(name, node);
		indexedNodes.add(node);
	}
	
	@Override
	public Node getNamedItem(String name) {
		return nodeMap.get(name);
	}

	@Override
	public Node setNamedItem(Node arg) throws DOMException {
		throw NodeImpl.createReadOnlyException();
	}

	@Override
	public Node removeNamedItem(String name) throws DOMException {
		throw NodeImpl.createReadOnlyException();
	}

	@Override
	public Node item(int index) {
		if(index >= 0 && index < indexedNodes.size())
			return indexedNodes.get(index);
		else
			return null;
	}

	@Override
	public int getLength() {
		return indexedNodes.size();
	}

	@Override
	public Node getNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		throw NodeImpl.createNoXMLSupportException();
	}

	@Override
	public Node setNamedItemNS(Node arg) throws DOMException {
		throw NodeImpl.createNoXMLSupportException();
	}

	@Override
	public Node removeNamedItemNS(String namespaceURI, String localName)
			throws DOMException {
		throw NodeImpl.createNoXMLSupportException();
	}
}
