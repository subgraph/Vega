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
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListImpl implements NodeList {

	static final NodeList emptyList = new NodeListImpl(new ArrayList<NodeImpl>(0));
	
	private final List<NodeImpl> nodes;
	
	public NodeListImpl(List<NodeImpl> nodes) {
		this.nodes = nodes;
	}
	
	@Override
	public Node item(int index) {
		if(index < 0 || index >= nodes.size())
			return null;
		return nodes.get(index);
	}

	@Override
	public int getLength() {
		return nodes.size();
	}

}
