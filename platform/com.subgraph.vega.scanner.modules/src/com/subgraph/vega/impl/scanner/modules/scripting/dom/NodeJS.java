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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLElement;

public class NodeJS extends ScriptableObject {
	
	static NodeJS domNodeToJS(Node node, DocumentJS document) {
		synchronized(document) {
			if(node == null)
				return null;
			NodeJS cachedNode = document.findCachedNode(node);
			if(cachedNode != null)
				return cachedNode;
			NodeJS newNode = createNodeJSFromDomNode(node, document);
			if(newNode != null)
				document.putCachedNode(node, newNode);
			return newNode;
		}
	}
	
	private static NodeJS createNodeJSFromDomNode(Node node, DocumentJS document) {
		if(node == null) {
			return null;
		} else if (node instanceof HTMLElement) {
			return HTMLElementJS.domHTMLElementToJS((HTMLElement) node, document);
		} else if (node instanceof HTMLDocument) {
			return document;
		} else if(node instanceof Comment) {
			return new CommentJS((Comment) node, document);
		} else if(node instanceof Text) {
			return new TextJS((Text) node, document);
		} else if(node instanceof CharacterData) {
			return new CharacterDataJS((CharacterData) node, document);
		} else if(node instanceof Attr) {
			return new AttrJS((Attr) node, document);
		} else if(node instanceof Document) {
			return document;
		} else if(node instanceof Element) {
			return new ElementJS((Element) node, document);
		} else {
			return new NodeJS(node, document);
		}
	}
	
	
	private static final long serialVersionUID = 1L;
	
	private Node node;
	private DocumentJS documentJS;
	public NodeJS() {
		this.node = null;
		this.documentJS = null;
	}
	
	public NodeJS(Node node, DocumentJS document) {
		if(node == null)
			throw new NullPointerException("Node cannot be null");
		this.node = node;
		this.documentJS = document;
	}

	protected void setNode(Node node) {
		this.node = node;
	}
	
	protected void setDocumentJS(DocumentJS document) {
		this.documentJS = document;
	}
	
	DocumentJS getDocumentJS() {
		return documentJS;
	}
	
	public void jsConstructor(Object ob) {		
	}
	
	protected void exportObject(Scriptable ob) {
		if(ob == null)
			return;
		final Scriptable scope = ScriptableObject.getTopLevelScope(this);
		ob.setParentScope(scope);
		ob.setPrototype(ScriptableObject.getClassPrototype(scope, ob.getClassName()));
	}
	
	protected NodeJS exportNode(Node node) {
		if(node == null)
			return null;
		NodeJS nodeJS = domNodeToJS(node, documentJS);
		exportObject(nodeJS);
		return nodeJS;
	}
	
	protected NodeListJS exportNodeList(NodeList nodeList) {
		if(nodeList == null)
			return null;
		NodeListJS nl = new NodeListJS(nodeList, ScriptableObject.getTopLevelScope(this), documentJS);
		exportObject(nl);
		return nl;
	}
	
	private Scriptable createNodeArray(Object[] nodes) {
		final Scriptable scope = ScriptableObject.getTopLevelScope(this);
		final Context cx = Context.getCurrentContext();
		Scriptable array =  cx.newArray(scope, nodes);
		exportObject(array);
		return array;
	}
	
	public Scriptable jsGet_attributes() {
		final NamedNodeMap attributes = node.getAttributes();
		final Object[] nodes = new Object[attributes.getLength()];
		
		for(int i = 0; i < attributes.getLength(); i++)  {
			nodes[i] = exportNode(attributes.item(i));
		}		
		return createNodeArray(nodes);
	}
	
	public Scriptable jsGet_childNodes() {
		return exportNodeList(node.getChildNodes());
	}
	
	public Scriptable jsGet_firstChild() {
		return exportNode(node.getFirstChild());
	}
	
	public Scriptable jsGet_lastChild() {
		return exportNode(node.getLastChild());
	}
	
	public String jsGet_localname() {
		return node.getLocalName();
	}
	
	public String jsGet_namespaceURI() {
		return node.getNamespaceURI();
	}
	
	public Scriptable jsGet_nextSibling() {
		return exportNode(node.getNextSibling());
	}
	
	public String jsGet_nodeName() {
		return node.getNodeName();
	}
	
	public int jsGet_nodeType() {
		return node.getNodeType();
	}
	
	public String jsGet_nodeValue() {
		return node.getNodeValue();
	}
	
	
	public Scriptable jsGet_ownerDocument() {
		return exportNode(node.getOwnerDocument());
	}
	
	public Scriptable jsGet_parentNode() {
		return exportNode(node.getParentNode());
	}
	
	public String jsGet_prefix() {
		return node.getPrefix();
	}
	
	public Scriptable jsGet_previousSibling() {
		return exportNode(node.getPreviousSibling());
	}
	
	public Scriptable jsFunction_appendChild(Scriptable newChild) throws DOMException {
		return null;
	}
	
	public Scriptable jsFunction_cloneNode(boolean deep) {
		return null;
	}
	
	public boolean jsFunction_hasAttributes() {
		return node.hasAttributes();
	}
	
	public boolean jsFunction_hasChildNodes() {
		return node.hasChildNodes();
	}
	
	public Scriptable jsFunction_insertBefore(Scriptable newChild, Scriptable refChild)  throws DOMException {
		return null;
	}
	
	public boolean jsFunction_isSupported(String feature, String version) {
		return node.isSupported(feature, version);
	}
	
	public void jsFunction_normalize() {
		node.normalize();
	}
	
	public Scriptable jsFunction_removeChild(Scriptable oldChild) throws DOMException {
		return null;
	}
	
	public Scriptable jsFunction_replaceChild(Scriptable newChild, Scriptable oldChild) throws DOMException {
		return null;
	}

	@Override
	public String getClassName() {
		return "Node";
	}
	
	public static void finishInit(Scriptable scope, FunctionObject ctor, Scriptable prototype) {
		ctor.defineProperty("ELEMENT_NODE", Node.ELEMENT_NODE, READONLY);
		ctor.defineProperty("ATTRIBUTE_NODE", Node.ATTRIBUTE_NODE, READONLY);
		ctor.defineProperty("TEXT_NODE", Node.TEXT_NODE, READONLY);
		ctor.defineProperty("CDATA_SECTION_NODE", Node.CDATA_SECTION_NODE, READONLY);
		ctor.defineProperty("PROCESSING_INSTRUCTION_NODE", Node.PROCESSING_INSTRUCTION_NODE, READONLY);
		ctor.defineProperty("COMMENT_NODE", Node.COMMENT_NODE, READONLY);
		ctor.defineProperty("DOCUMENT_NODE", Node.DOCUMENT_NODE, READONLY);
		ctor.defineProperty("DOCUMENT_TYPE_NODE", Node.DOCUMENT_TYPE_NODE, READONLY);
		ctor.defineProperty("DOCUMENT_FRAGMENT_NODE", Node.DOCUMENT_FRAGMENT_NODE, READONLY);
	}
	
	public int jsFunction_compareDocumentPosition(Scriptable other) {
		if(!(other instanceof NodeJS)) {
			throw Context.reportRuntimeError("compareDocumentPosition must be called with a Node argument");
		}
		final Node otherNode = ((NodeJS)other).node;
		return node.compareDocumentPosition(otherNode);
	}
}
