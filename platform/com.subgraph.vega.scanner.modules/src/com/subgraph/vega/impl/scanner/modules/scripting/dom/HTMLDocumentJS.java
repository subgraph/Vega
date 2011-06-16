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
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.html2.HTMLDocument;

public class HTMLDocumentJS extends DocumentJS {

	private static final long serialVersionUID = 1L;

	private HTMLDocument htmlDocument;
	
	public HTMLDocumentJS() {
		htmlDocument = null;
	}
	
	public HTMLDocumentJS(HTMLDocument document) {
		super(document);
		this.htmlDocument = document;
	}
	
	@Override
	public void jsConstructor(Object ob) {
		final HTMLDocument d = (HTMLDocument) Context.jsToJava(ob, HTMLDocument.class);
		this.htmlDocument = d;
		this.document = d;
		setNode(d);
		setDocumentJS(this);
	}
	
	@Override
	public String getClassName() {
		return "HTMLDocument";
	}
	
	public Scriptable jsGet_anchors() {
		HTMLCollectionJS collection = new HTMLCollectionJS(htmlDocument.getAnchors(), ScriptableObject.getTopLevelScope(this), this);
		exportObject(collection);
		return collection;
	}
	
	public Scriptable jsGet_applets() {
		HTMLCollectionJS collection = new HTMLCollectionJS(htmlDocument.getApplets(), ScriptableObject.getTopLevelScope(this), this);
		exportObject(collection);
		return collection;
	}
	
	public Scriptable jsGet_body() {
		return exportNode(htmlDocument.getBody());
	}
	
	public String jsGet_cookie() {
		return htmlDocument.getCookie();
	}
	
	public String jsGet_domain() {
		return htmlDocument.getDomain();
	}
	
	public Scriptable jsGet_forms() {
		HTMLCollectionJS collection = new HTMLCollectionJS(htmlDocument.getForms(), ScriptableObject.getTopLevelScope(this), this);
		exportObject(collection);
		return collection;
	}
	
	public Scriptable jsGet_images() {
		HTMLCollectionJS collection = new HTMLCollectionJS(htmlDocument.getImages(), ScriptableObject.getTopLevelScope(this), this);
		exportObject(collection);
		return collection;
	}
	
	public Scriptable jsGet_links() {
		HTMLCollectionJS collection = new HTMLCollectionJS(htmlDocument.getLinks(), ScriptableObject.getTopLevelScope(this), this);
		exportObject(collection);
		return collection;
	}
	
	public String jsGet_referrer() {
		return htmlDocument.getReferrer();
	}
	
	public String jsGet_title() {
		return htmlDocument.getTitle();
	}
	
	public String jsGet_URL() {
		return htmlDocument.getURL();
	}
	
	public void jsFunction_close() {
		htmlDocument.close();
	}
	
	public Scriptable jsFunction_getElementsByName(String elementName) {		
		final NodeListJS nodeList = new NodeListJS(htmlDocument.getElementsByName(elementName), ScriptableObject.getTopLevelScope(this), this);
		exportObject(nodeList);
		return nodeList;
	}
	
	public void jsFunction_open() {
		htmlDocument.open();
	}
	
	public void jsFunction_write(String text) {
		htmlDocument.write(text);
	}
	
	public void jsFunction_writeln(String text) {
		htmlDocument.writeln(text);
	}
}
