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
import org.w3c.dom.Attr;

public class AttrJS extends NodeJS {
	
	private static final long serialVersionUID = 1L;
	private final Attr attr;
	
	public AttrJS() {
		this.attr = null;
	}
	
	public AttrJS(Attr attr, DocumentJS document) {
		super(attr, document);
		this.attr = attr;
	}
	
	@Override
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "Attr";
	}
	
	public String jsGet_name() {
		return attr.getName();
	}
	
	public Scriptable jsGet_ownerElement() {
		return exportNode(attr.getOwnerElement());
	}
	
	public boolean jsGet_specified() {
		return attr.getSpecified();
	}
	
	public String jsGet_value() {
		return attr.getValue();
	}

}
