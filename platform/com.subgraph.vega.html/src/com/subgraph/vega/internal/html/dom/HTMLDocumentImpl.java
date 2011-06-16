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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLElement;


public class HTMLDocumentImpl extends DocumentImpl implements HTMLDocument {

	public static HTMLDocumentImpl createFromJsoup(Document jsoupDocument) {
		HTMLDocumentImpl htmlDocument = new HTMLDocumentImpl(jsoupDocument);
		htmlDocument.setOwnerDocument(htmlDocument);
		return htmlDocument;
	}
	
	HTMLDocumentImpl(org.jsoup.nodes.Document jsoupDocument) {
		super(jsoupDocument);
	}

	@Override
	public String getTitle() {
		Elements elements = jsoupDocument.getElementsByTag("title");
		Element titleElement = elements.first();
		if(titleElement == null)
			return null;
		else
			return titleElement.text();
	}

	@Override
	public void setTitle(String title) {
		
	}

	@Override
	public String getReferrer() {
		return null;
	}

	@Override
	public String getDomain() {
		return null;
	}

	@Override
	public String getURL() {
		return null;
	}

	@Override
	public HTMLElement getBody() {
		Elements elements = jsoupDocument.getElementsByTag("body");
		Element bodyElement = elements.first();
		if(bodyElement == null)
			return null;
		else
			return new HTMLElementImpl(bodyElement, this);
	}

	@Override
	public void setBody(HTMLElement body) {		
	}

	@Override
	public HTMLCollection getImages() {
		return selectCollection("img");
	}

	@Override
	public HTMLCollection getApplets() {
		return selectCollection("applet, object[classid]");
	}

	@Override
	public HTMLCollection getLinks() {
		List<HTMLElementImpl> links = new ArrayList<HTMLElementImpl>();
		for(Element e: jsoupDocument.select("a[href], area[href]")) {
			links.add(new HTMLLinkElementImpl(e, getOwnerDocument()));
		}
		return new HTMLCollectionImpl(links);
	}

	@Override
	public HTMLCollection getForms() {
		return selectCollection("form");
	}

	@Override
	public HTMLCollection getAnchors() {
		return selectCollection("a[name]");
	}

	@Override
	public String getCookie() {
		return "";
	}

	@Override
	public void setCookie(String cookie) throws DOMException {
		
	}

	@Override
	public void open() {		
	}

	@Override
	public void close() {		
	}

	@Override
	public void write(String text) {
		
	}

	@Override
	public void writeln(String text) {
	}

	@Override
	public NodeList getElementsByName(String elementName) {
		return selectCollection("*[name="+ elementName +"]").toNodeList();
	}
	
	private HTMLCollectionImpl selectCollection(String query) {
		return new HTMLCollectionImpl( jsoupDocument.select(query), this);
	}
}
