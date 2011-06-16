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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLSelectElement;

public class HTMLSelectElementImpl extends HTMLElementImpl implements HTMLSelectElement {

	private final HTMLFormElement form;
	private HTMLOptionsCollectionImpl options;
	
	HTMLSelectElementImpl(Element jsoupElement, HTMLFormElement form, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
		this.form = form;
	}
	
	HTMLSelectElementImpl(Element jsoupElement, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
		Element formElement = HTMLElementImpl.findEnclosingFormElement(jsoupElement);
		this.form = new HTMLFormElementImpl(formElement, ownerDocument);
	}

	@Override
	public String getType() {
		if(hasAttribute("multiple"))
			return "select-multiple";
		else
			return "select-one";
	}

	@Override
	public int getSelectedIndex() {
		return getOptions().getSelectedIndex();
	}
	
	@Override
	public void setSelectedIndex(int selectedIndex) {		
	}

	@Override
	public String getValue() {
		return getOptions().getValue();
	}

	@Override
	public void setValue(String value) {		
	}

	@Override
	public int getLength() {
		return getOptions().getLength();
	}

	@Override
	public void setLength(int length) throws DOMException {		
	}

	@Override
	public HTMLFormElement getForm() {
		return form;
	}

	@Override
	public HTMLOptionsCollectionImpl getOptions() {
		if(options == null) {
			options = new HTMLOptionsCollectionImpl(jsoupElement.select("option"), this, getOwnerDocument());
		}
		return options;
	}

	@Override
	public boolean getDisabled() {
		return hasAttribute("disabled");
	}

	@Override
	public void setDisabled(boolean disabled) {		
	}

	@Override
	public boolean getMultiple() {
		return hasAttribute("multiple");
	}

	@Override
	public void setMultiple(boolean multiple) {		
	}

	@Override
	public String getName() {
		return getAttribute("name");
	}

	@Override
	public void setName(String name) {		
	}

	@Override
	public int getSize() {
		return getIntAttribute("size");
	}

	@Override
	public void setSize(int size) {		
	}

	@Override
	public int getTabIndex() {
		return getIntAttribute("tabindex");
	}

	@Override
	public void setTabIndex(int tabIndex) {		
	}

	@Override
	public void add(HTMLElement element, HTMLElement before)
			throws DOMException {		
	}

	@Override
	public void remove(int index) {		
	}

	@Override
	public void blur() {		
	}

	@Override
	public void focus() {		
	}

}
