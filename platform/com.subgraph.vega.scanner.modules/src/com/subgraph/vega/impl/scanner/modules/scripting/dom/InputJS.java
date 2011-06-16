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
import org.w3c.dom.html2.HTMLInputElement;

public class InputJS extends HTMLElementJS {
	
	private static final long serialVersionUID = 1L;
	
	private final HTMLInputElement inputElement;
	
	public InputJS() {
		this.inputElement = null;
	}
	
	public InputJS(HTMLInputElement element, DocumentJS document) {
		super(element, document);
		this.inputElement = element;
	}
	
	@Override
	public void jsConstructor(Object ob) {
		
	}
	
	@Override
	public String getClassName() {
		return "Input";
	}
	
	public String jsGet_accept() {
		return inputElement.getAccept();
	}
	
	public String jsGet_accessKey() {
		return inputElement.getAccessKey();
	}
	
	public String jsGet_align() {
		return inputElement.getAlign();
	}
	
	public String jsGet_alt() {
		return inputElement.getAlt();
	}
	
	public boolean jsGet_checked() {
		return inputElement.getChecked();
	}
	
	public boolean jsGet_defaultChecked() {
		return inputElement.getDefaultChecked();
	}
	
	public String jsGet_defaultValue() {
		return inputElement.getDefaultValue();
	}
	
	public boolean jsGet_disabled() {
		return inputElement.getDisabled();
	}
	
	public Scriptable jsGet_form() {
		return exportNode(inputElement.getForm());
	}
	
	public int jsGet_maxLength() {
		return inputElement.getMaxLength();
	}
	
	public String jsGet_name() {
		return inputElement.getName();
	}
	
	public boolean jsGet_readOnly() {
		return inputElement.getReadOnly();
	}
	
	public int jsGet_size() {
		return inputElement.getSize();
	}
	
	public String jsGet_src() {
		return inputElement.getSrc();
	}
	
	public int jsGet_tabIndex() {
		return inputElement.getTabIndex();
	}
	
	public String jsGet_type() {
		return inputElement.getType();
	}
	
	public String jsGet_useMap() {
		return inputElement.getUseMap();
	}
	
	public String jsGet_value() {
		return inputElement.getValue();
	}
	
	public void jsFunction_blur() {
		inputElement.blur();
	}
	
	public void jsFunction_click() {
		inputElement.click();
	}
	
	public void jsFunction_focus() {
		inputElement.focus();
	}
	
	public Scriptable jsGet_onblur() {
		return null;
	}
	
	public Scriptable jsGet_onchange() {
		return null;
	}
	
	public Scriptable jsGet_onfocus() {
		return null;
	}
	
	public void jsFunction_select() {
		inputElement.select();
	}
}
