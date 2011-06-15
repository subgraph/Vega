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

import org.w3c.dom.html2.HTMLAnchorElement;

public class AnchorJS extends HTMLElementJS {

	private static final long serialVersionUID = 1L;
	
	public AnchorJS() {
	}
	
	public AnchorJS(HTMLAnchorElement element, DocumentJS document) {
		super(element, document);
	}
	
	@Override 
	public void jsConstructor(Object ob) {
		
	}
	
	@Override
	public String getClassName() {
		return "Anchor";
	}
	
	public String jsGet_name() {
		return element.getAttribute("name");
	}
	
	public void jsFunction_focus() {
		
	}
}
