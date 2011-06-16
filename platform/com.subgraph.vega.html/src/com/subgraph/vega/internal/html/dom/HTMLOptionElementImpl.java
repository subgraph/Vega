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
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLOptionElement;

public class HTMLOptionElementImpl extends HTMLElementImpl implements HTMLOptionElement {

	private final HTMLSelectElementImpl select;
	private final HTMLFormElement form;
	private final int index;
	
	HTMLOptionElementImpl(Element jsoupElement, HTMLSelectElementImpl select, int index, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
		this.select = select;
		this.form = select.getForm();
		this.index = index;
	}
	
	HTMLOptionElementImpl(Element jsoupElement, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
		Element formElement = HTMLElementImpl.findEnclosingFormElement(jsoupElement);
		this.form = new HTMLFormElementImpl(formElement, ownerDocument);
		Element selectElement = HTMLElementImpl.findEnclosingSelectElement(jsoupElement);
		this.select = new HTMLSelectElementImpl(selectElement, form, ownerDocument);
		this.index = calculateIndex(jsoupElement);
	}
	
	private int calculateIndex(Element jsoupElement) {
		Element e = jsoupElement;
		int index = 0;
		while(e.previousElementSibling() != null) {
			e = e.previousElementSibling();
			index += 1;
		}
		return index;
	}

	@Override
	public HTMLFormElement getForm() {
		return form;
	}

	@Override
	public boolean getDefaultSelected() {
		return hasAttribute("selected");
	}

	@Override
	public void setDefaultSelected(boolean defaultSelected) {
		
	}

	@Override
	public String getText() {
		return getTextContent();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean getDisabled() {
		return hasAttribute("disabled");
	}

	@Override
	public void setDisabled(boolean disabled) {		
	}

	@Override
	public String getLabel() {
		return getAttribute("label");
	}

	@Override
	public void setLabel(String label) {		
	}

	@Override
	public boolean getSelected() {
		return hasAttribute("selected") || select.getSelectedIndex() == index;
	}

	@Override
	public void setSelected(boolean selected) {		
	}

	@Override
	public String getValue() {
		return getAttribute("value");
	}

	@Override
	public void setValue(String value) {		
	}
}
