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

import org.mozilla.javascript.Scriptable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ElementJS extends NodeJS {

	private static final long serialVersionUID = 1L;

	protected final Element element;
	
	public ElementJS() {
		this.element = null;
	}
	
	public ElementJS(Element element, DocumentJS document) {
		super(element, document);
		this.element = element;
	}
	
	@Override
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "Element";
	}
	
	public String jsGet_tagName() {
		return element.getTagName();
	}
	
	public void jsFunction_addEventListener(String type, Scriptable listener, boolean useCapture) {
		
	}
	
	public void jsFunction_attachEvent(String type, Scriptable listener) {
		
	}
	
	public void jsFunction_detachEvent(String type, Scriptable listener) {
		
	}
	
	public boolean jsFunction_dispatchEvent(Scriptable evt) {
		return false;
	}
	
	public String jsFunction_getAttribute(String name) {
		return element.getAttribute(name);
	}
	
	public Scriptable jsFunction_getAttributeNode(String name) {
		return exportNode(element.getAttributeNode(name));
	}
	
	public Scriptable jsFunction_getAttributeNodeNS(String namespaceURI, String localName) {
		return exportNode(element.getAttributeNodeNS(namespaceURI, localName));
	}
	
	public String jsFunction_getAttributeNS(String namespaceURI, String localName) {
		return element.getAttributeNS(namespaceURI, localName);
	}
	
	public Scriptable jsFunction_getElementsByTagName(String name) {
		return exportNodeList(element.getElementsByTagName(name));		
	}
	
	public Scriptable jsFunction_getElementsByTagNameNS(String namespaceURI, String localName) {
		return exportNodeList(element.getElementsByTagNameNS(namespaceURI, localName));
	}
	
	public boolean jsFunction_hasAttribute(String name) {
		return element.hasAttribute(name);
	}
	
	public boolean jsFunction_hasAttributeNS(String namespaceURI, String localName) {
		return element.hasAttributeNS(namespaceURI, localName);
	}
	
	public void jsFunction_removeAttribute(String name) {
		element.removeAttribute(name);
	}
	
	public Scriptable jsFunction_removeAttributeNode(Scriptable oldAttr) {
		return null;
	}
	
	public void jsFunction_removeAttributeNS(String namespaceURI, String localName) {
		element.removeAttributeNS(namespaceURI, localName);
	}
	
	public void jsFunction_removeEventListener(String type, Scriptable listener, boolean useCapture) {
		
	}
	
	public void jsFunction_setAttribute(String name, String value) throws DOMException {
		element.setAttribute(name, value);
	}
	
	public Scriptable jsFunction_setAttributeNode(Scriptable newAttr) throws DOMException {
		
		return null;
	}
	
	public Scriptable jsFunction_setAttributeNodeJS(Scriptable newAttr) throws DOMException {
		return null;
	}
	
	public void jsFunction_setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
		element.setAttributeNS(namespaceURI, qualifiedName, value);
	}
}
