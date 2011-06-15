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

import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;


public class HTMLCollectionImpl implements HTMLCollection {

	private List<HTMLElementImpl> byIndex = new ArrayList<HTMLElementImpl>();
	private Map<String, HTMLElement> byId = new HashMap<String, HTMLElement>();
	private Map<String, HTMLElement> byName = new HashMap<String, HTMLElement>();
	
	HTMLCollectionImpl(List<HTMLElementImpl> elements) {
		for(HTMLElementImpl e: elements) {
			addElement(e);
		}
	}
	
	HTMLCollectionImpl(List<Element> jsoupElements, Document document) {
		for(Element e: jsoupElements) {
			HTMLElementImpl htmlElement = HTMLElementImpl.create(e, document);
			addElement(htmlElement);
		}
	}
	
	private void addElement(HTMLElementImpl element) {
		String id = element.getAttribute("id");
		String name = element.getAttribute("name");
		byIndex.add(element);
		if(id != null)
			byId.put("id", element);
		if(name != null)
			byName.put("name", element);
	}
	
	@Override
	public int getLength() {
		return byIndex.size();
	}

	@Override
	public Node item(int index) {
		if(index >= 0 && index < byIndex.size())
			return byIndex.get(index);
		else
			return null;
	}

	@Override
	public Node namedItem(String name) {
		if(byId.containsKey(name))
			return byId.get(name);
		return byName.get(name);
	}
	
	NodeList toNodeList() {
		List<NodeImpl> nodes = new ArrayList<NodeImpl>();
		nodes.addAll(byIndex);
		return new NodeListImpl(nodes);
	}

}
