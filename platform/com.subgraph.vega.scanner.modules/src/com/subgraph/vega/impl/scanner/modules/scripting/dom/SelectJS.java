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
import org.w3c.dom.html2.HTMLSelectElement;

public class SelectJS extends HTMLElementJS {

	private static final long serialVersionUID = 1L;
	
	private final HTMLSelectElement selectElement;
	
	public SelectJS() {
		selectElement = null;
	}
	
	public SelectJS(HTMLSelectElement element, DocumentJS document) {
		super(element, document);
		this.selectElement = element;
	}
	
	@Override
	public void jsConstructor(Object ob) {
		
	}
	
	@Override
	public String getClassName() {
		return "Select";
	}
	
	public Scriptable jsGet_form() {
		return exportNode(selectElement.getForm());
	}
	
	public int jsGet_length() {
		return selectElement.getLength();
	}
	
	public Object jsGet_options() {
		return null;
	}
	
	public int jsGet_selectedIndex() {
		return selectElement.getSelectedIndex();
	}
	
	public String jsGet_type() {
		return selectElement.getType();
	}
	
	public boolean jsGet_disabled() {
		return selectElement.getDisabled();
	}
	
	public boolean jsGet_multiple() {
		return selectElement.getMultiple();
	}
	
	public String jsGet_name() {
		return selectElement.getName();
	}
	
	public int jsGet_size() {
		return selectElement.getSize();
	}
	
	public int jsGet_tabIndex() {
		return selectElement.getTabIndex();
	}
	
	public void jsFunction_add(Scriptable element, Scriptable before) {
		
	}
	
	public void jsFunction_blur() {
		
	}
	public void jsFunction_focus() {
		
	}
	
	public Scriptable jsGet_onchange() {
		return null;
	}
	
	public void jsFunction_remove(int index) {
		selectElement.remove(index);
	}
}
