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

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DocumentJS extends NodeJS {

	private static final long serialVersionUID = 1L;
	protected Document document;
	private Map<Node, NodeJS> nodeCache = new HashMap<Node, NodeJS>();
	
	public DocumentJS() {
		this.document = null;
	}
	
	NodeJS findCachedNode(Node node) {
		return nodeCache.get(node);
	}
	void putCachedNode(Node node, NodeJS nodeJS) {
		nodeCache.put(node, nodeJS);
	}
	
	public DocumentJS(Document document) {
		super(document, null);
		this.document = document;
		setDocumentJS(this);
	}
	
	public void jsConstructor(Object ob) {
		final Document d = (Document) Context.jsToJava(ob, Document.class);
		this.document = d;
		setNode(d);
		setDocumentJS(this);
	}
	
	@Override
	public String getClassName() {
		return "Document";
	}
	
	public Scriptable jsGet_doctype() {
		return null;
	}
	
	public Scriptable jsGet_documentElement() {
		return exportNode(document.getDocumentElement());
	}
	
	public Scriptable jsGet_implementation() {
		return null;
	}
	
	public void jsFunction_addEventListener(String type, Scriptable listener, boolean useCapture) {
		
	}
	
	public void jsFunction_attachEvent(String type, Scriptable listener) {
		
	}
	
	public Scriptable jsFunction_createAttribute(String name) {
		return exportNode(document.createAttribute(name));
	}
	
	public Scriptable jsFunction_createAttributeNS(String namespaceURI, String qualifiedName) {
		return exportNode(document.createAttributeNS(namespaceURI, qualifiedName));
	}
	
	public Scriptable jsFunction_createCDataSection(String data) throws DOMException {
		return exportNode(document.createCDATASection(data));
	}
	
	public Scriptable jsFunction_createComment(String data) {
		return exportNode(document.createComment(data));
	}
	
	public Scriptable jsFunction_createDocumentFragment() {
		return exportNode(document.createDocumentFragment());
	}
	
	public Scriptable jsFunction_createElement(String tagName) {
		return exportNode(document.createElement(tagName));
	}
	
	public Scriptable jsFunction_createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
		return exportNode(document.createElementNS(namespaceURI, qualifiedName));
	}
	
	public Scriptable jsFunction_createEvent(String eventType) throws DOMException {
		return null;
	}
	
	public Scriptable jsFunction_createProcessingInstruction(String target, String data) throws DOMException {
		return exportNode(document.createProcessingInstruction(target, data));
	}
	
	public Scriptable jsFunction_createRange() {
		return null;
	}
	
	public Scriptable jsFunction_createTextNode(String data) {
		return exportNode(document.createTextNode(data));
	}
	
	public void jsFunction_detachEvent(String type, Scriptable listener) {
		
	}
	
	public boolean jsFunction_dispatchEvent(Scriptable evt) {
		return false;
	}
	
	public Scriptable jsFunction_getElementById(String elementId) {
		return exportNode(document.getElementById(elementId));
	}

	public Scriptable jsFunction_getElementsByTagName(String tagName) {
		return exportNodeList(document.getElementsByTagName(tagName));
	}
	
	public Scriptable jsFunction_getElementsByTagNameNS(String namespaceURI, String localName) {
		return exportNodeList(document.getElementsByTagNameNS(namespaceURI, localName));
	}
	
	public Scriptable jsFunction_importNode(Scriptable importedNode, boolean deep) throws DOMException {
		return null;
	}
	
	public void jsFunction_removeEventListener(String type, Scriptable listener, boolean useCapture) {
		
	}	
	
}
