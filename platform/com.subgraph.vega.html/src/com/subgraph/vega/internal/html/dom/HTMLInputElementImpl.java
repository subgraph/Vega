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
import org.w3c.dom.html2.HTMLInputElement;

public class HTMLInputElementImpl extends HTMLElementImpl implements HTMLInputElement {

	private final HTMLFormElement form;
	
	HTMLInputElementImpl(Element jsoupElement, HTMLFormElement form, Document ownerDocument) {
		super(jsoupElement, ownerDocument);
		this.form = form;
	}

	@Override
	public String getDefaultValue() {
		String type = getType();
		if(type != null && (type.equals("text") || type.equals("file") || type.equals("password"))) {
			return jsoupElement.attr("value");
		} else {
			return null;
		}
	}

	@Override
	public void setDefaultValue(String defaultValue) {
	}

	@Override
	public boolean getDefaultChecked() {
		String type = getType();
		if(type != null && (type.equals("checkbox") || type.equals("radio"))) {
			return hasAttribute("checked");
		} else {
			return false;
		}
	}

	@Override
	public void setDefaultChecked(boolean defaultChecked) {		
	}

	@Override
	public HTMLFormElement getForm() {
		return form;
	}

	@Override
	public String getAccept() {
		return getAttribute("accept");
	}

	@Override
	public void setAccept(String accept) {
	}

	@Override
	public String getAccessKey() {
		return getAttribute("accesskey");
	}

	@Override
	public void setAccessKey(String accessKey) {
	}

	@Override
	public String getAlign() {
		return getAttribute("align");
	}

	@Override
	public void setAlign(String align) {		
	}

	@Override
	public String getAlt() {
		return getAttribute("alt");
	}

	@Override
	public void setAlt(String alt) {		
	}

	@Override
	public boolean getChecked() {
		return hasAttribute("checked");
	}

	@Override
	public void setChecked(boolean checked) {		
	}

	@Override
	public boolean getDisabled() {
		return hasAttribute("disabled");
	}

	@Override
	public void setDisabled(boolean disabled) {		
	}

	@Override
	public int getMaxLength() {
		return getIntAttribute("maxlength");
	}

	@Override
	public void setMaxLength(int maxLength) {		
	}

	@Override
	public String getName() {
		return getAttribute("name");
	}

	@Override
	public void setName(String name) {		
	}

	@Override
	public boolean getReadOnly() {
		return hasAttribute("readonly");
	}

	@Override
	public void setReadOnly(boolean readOnly) {		
	}

	@Override
	public int getSize() {
		return getIntAttribute("size");
	}

	@Override
	public void setSize(int size) {		
	}

	@Override
	public String getSrc() {
		return getAttribute("src");
	}

	@Override
	public void setSrc(String src) {		
	}

	@Override
	public int getTabIndex() {
		return getIntAttribute("tabindex");
	}

	@Override
	public void setTabIndex(int tabIndex) {		
	}

	@Override
	public String getType() {
		return getAttribute("type");
	}

	@Override
	public void setType(String type) {		
	}

	@Override
	public String getUseMap() {
		return getAttribute("usemap");
	}

	@Override
	public void setUseMap(String useMap) {
		
	}

	@Override
	public String getValue() {
		return getAttribute("value");
	}

	@Override
	public void setValue(String value) {		
	}

	@Override
	public void blur() {		
	}

	@Override
	public void focus() {		
	}

	@Override
	public void select() {		
	}

	@Override
	public void click() {		
	}
}
