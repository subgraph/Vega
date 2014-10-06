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

import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLFormElement;

public class HTMLFormElementImpl extends HTMLElementImpl implements HTMLFormElement {

	private HTMLCollection formElements;

	HTMLFormElementImpl(Element jsoupElement, Document ownerDocument) {
		
		super(jsoupElement, ownerDocument);
	}

	@Override
	public HTMLCollection getElements() {
		if(formElements != null)
			return formElements;
		
		final List<HTMLElementImpl> elementList = new ArrayList<HTMLElementImpl>();
		
		Elements fe;
		fe = ((FormElement) jsoupElement).elements();
		
		for(Element e: fe) {
			addFormElementsToList(e, elementList);
		}
		
		formElements = new HTMLCollectionImpl(elementList);
		return formElements;		
	}
	
	private void addFormElementsToList(Element jsoupElement, List<HTMLElementImpl> formElements) {
		String tag = jsoupElement.tagName().toUpperCase();
		if("INPUT".equals(tag)) {
			formElements.add(new HTMLInputElementImpl(jsoupElement, this, getOwnerDocument()));
			return;
		} else if("SELECT".equals(tag)) {
			HTMLSelectElementImpl selectElement = new HTMLSelectElementImpl(jsoupElement, this, getOwnerDocument());
			
			formElements.add(selectElement);
			HTMLOptionsCollectionImpl options = selectElement.getOptions();
			for(int i = 0; i < options.getLength(); i++) {
				formElements.add(options.item(i));
			}
			return;
		} else {
			formElements.add(new HTMLElementImpl(jsoupElement, getOwnerDocument()));
			return;
		}
	}
	
	@Override
	public int getLength() {
		return getElements().getLength();
	}

	@Override
	public String getName() {
		return jsoupElement.attr("name");
	}

	@Override
	public void setName(String name) {		
	}

	@Override
	public String getAcceptCharset() {
		return jsoupElement.attr("accept");
	}

	@Override
	public void setAcceptCharset(String acceptCharset) {		
	}

	@Override
	public String getAction() {
		return jsoupElement.attr("action");
	}

	@Override
	public void setAction(String action) {		
	}

	@Override
	public String getEnctype() {
		return jsoupElement.attr("enctype");
	}

	@Override
	public void setEnctype(String enctype) {		
	}

	@Override
	public String getMethod() {
		return jsoupElement.attr("method");
	}

	@Override
	public void setMethod(String method) {		
	}

	@Override
	public String getTarget() {
		return jsoupElement.attr("target");
	}

	@Override
	public void setTarget(String target) {		
	}

	@Override
	public void submit() {		
	}

	@Override
	public void reset() {		
	}
}
