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

import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLFormElement;

import com.subgraph.vega.api.html.IInnerHtmlProvidingElement;


public class HTMLElementImpl extends ElementImpl implements HTMLElement, IInnerHtmlProvidingElement {

	static Element findEnclosingFormElement(Element insideElement) {
		Element e = insideElement;
		while(e != null) {
			if(e.tagName().toUpperCase().equals("FORM"))
				return e;
			e = e.parent();
		}
		return null;
	}
	
	static Element findEnclosingSelectElement(Element insideElement) {
		
		Element e = insideElement;
		while(e != null) {
			if(e.tagName().toUpperCase().equals("SELECT"))
				return e;
			e = e.parent();
		}
		return null;
	}
	
	static HTMLElementImpl create(Element jsoupElement, Document ownerDocument) {
		final String tag = jsoupElement.tagName().toUpperCase();
		if(tag == null)
			throw new IllegalArgumentException();
		if(tag.equals("FORM")) {
			return new HTMLFormElementImpl(jsoupElement, ownerDocument);
		} else if(tag.equals("INPUT")) {
			Element jsoupFormElement = findEnclosingFormElement(jsoupElement);
			if(jsoupFormElement == null)
				return new HTMLInputElementImpl(jsoupElement, null, ownerDocument);
			HTMLFormElement form = new HTMLFormElementImpl(jsoupFormElement, ownerDocument);
			return new HTMLInputElementImpl(jsoupElement, form, ownerDocument);
		} else if(tag.equals("A")) {
			return new HTMLAnchorElementImpl(jsoupElement, ownerDocument);
		} else if(tag.equals("OPTION")) {
			return new HTMLOptionElementImpl(jsoupElement, ownerDocument);
		} else if(tag.equals("SELECT")) {
			return new HTMLSelectElementImpl(jsoupElement, ownerDocument);
		} else {
			return new HTMLElementImpl(jsoupElement, ownerDocument);
		}
	}
	HTMLElementImpl(Element jsoupElement, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
	}

	@Override
	public String getId() {
		return jsoupElement.attr("id");
	}

	@Override
	public void setId(String id) {
	}

	@Override
	public String getTitle() {
		return jsoupElement.attr("title");
	}

	@Override
	public void setTitle(String title) {		
	}

	@Override
	public String getLang() {
		return jsoupElement.attr("lang");
	}

	@Override
	public void setLang(String lang) {		
	}

	@Override
	public String getDir() {
		return jsoupElement.attr("dir");
	}

	@Override
	public void setDir(String dir) {		
	}

	@Override
	public String getClassName() {
		return jsoupElement.attr("class");
	}

	@Override
	public void setClassName(String className) {		
	}
	
	protected int getIntAttribute(String name) {
		final String str = getAttribute(name);
		if(str == null)
			return 0;
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	@Override
	public String getInnerHtml() {
		return jsoupElement.html();
	}
}
