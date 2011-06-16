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
package com.subgraph.vega.impl.scanner.modules.scripting.dom;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListJS extends ScriptableObject {
	
	private static final long serialVersionUID = 1L;

	private final List<Scriptable> nodeList = new ArrayList<Scriptable>();
	
	public NodeListJS() {
	}
	
	public NodeListJS(NodeList nodeList, Scriptable scope, DocumentJS document) {
		for(int i = 0; i < nodeList.getLength(); i++) {
			addNode(nodeList.item(i), scope, document);
		}
	}
	
	private void addNode(Node n, Scriptable scope, DocumentJS document) {
		Scriptable ob = NodeJS.domNodeToJS(n, document);
		ob.setParentScope(scope);
		ob.setPrototype(ScriptableObject.getClassPrototype(scope, ob.getClassName()));
		nodeList.add(ob);
	}
	
	public void jsConstructor(Object ob) {
		
	}

	public int jsGet_length() {
		return nodeList.size();
	}
	
	public Scriptable jsFunction_item(int index) {
		if(index < 0 || index >= nodeList.size())
			return null;
		return nodeList.get(index);
	}

	@Override
	public String getClassName() {
		return "NodeList";
	}
	
	@Override
	public Object get(int index, Scriptable start) {
		Scriptable ob = jsFunction_item(index);
		if(ob != null)
			return ob;
		else
			return super.get(index, start);
	}

}
