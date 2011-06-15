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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;

public class HTMLCollectionJS extends ScriptableObject {

	private static final long serialVersionUID = 1L;

	private final List<Scriptable> byIndex = new ArrayList<Scriptable>();
	private final Map<String, Scriptable> byName = new HashMap<String, Scriptable>();
	private final Map<String, Scriptable> byId = new HashMap<String, Scriptable>();
	
	public HTMLCollectionJS() {
		
	}
	
	public HTMLCollectionJS(HTMLCollection elements, Scriptable scope, DocumentJS document) {
		for(int i = 0; i < elements.getLength(); i++) {
			Node n = elements.item(i);
			if(n instanceof HTMLElement) {
				addHTMLElement((HTMLElement) n, scope, document);
			}			
		}
	}
	
	private void addHTMLElement(HTMLElement element, Scriptable scope, DocumentJS document) {
		Scriptable ob = HTMLElementJS.domHTMLElementToJS(element, document);
		ob.setParentScope(scope);
		ob.setPrototype(ScriptableObject.getClassPrototype(scope, ob.getClassName()));
		byIndex.add(ob);
		String id = element.getId();
		String name = element.getAttribute("name");
		if(id != null)
			byId.put(id, ob);
		if(name != null)
			byName.put(name, ob);
	}
	
	public void jsConstructor(Object ob) {
		
	}
	
	@Override
	public String getClassName() {
		return "HTMLCollection";
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object ob = jsFunction_namedItem(name);
		if(ob != null)
			return ob;
		else
			return super.get(name, start);
	}
	
	public int jsGet_length() {
		return byIndex.size();
	}
	
	public Scriptable jsFunction_item(int index) {
		if(index < 0 || index >= byIndex.size())
			return null;
		return byIndex.get(index);
	}
	
	public Object jsFunction_namedItem(String name) {
		if(byId.containsKey(name)) {
			return byId.get(name);
		} else {
			return byName.get(name);
		}
	}
}
