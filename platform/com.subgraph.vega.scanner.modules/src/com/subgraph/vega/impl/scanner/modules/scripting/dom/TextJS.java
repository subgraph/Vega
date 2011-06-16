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
import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class TextJS extends CharacterDataJS {
	
	private static final long serialVersionUID = 1L;

	private final Text textNode;
	
	public TextJS() {
		this.textNode = null;
	}
	
	public TextJS(Text textNode, DocumentJS document) {
		super(textNode, document);
		this.textNode = textNode;
	}
	
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "Text";
	}

	public Scriptable jsFunction_splitText(int offset) throws DOMException {
		return exportNode(textNode.splitText(offset));
	}

}
