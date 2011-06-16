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

import org.jsoup.nodes.TextNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextImpl extends CharacterDataImpl implements Text {

	TextImpl(TextNode textNode, Document ownerDocument) {
		super(textNode, textNode.getWholeText(), ownerDocument);
	}
	
	@Override
	public String getNodeName() {
		return "#text";
	}
	
	@Override
	public short getNodeType() {
		return Node.TEXT_NODE;
	}

	@Override
	public Text splitText(int offset) throws DOMException {
		throw createReadOnlyException();
	}

	@Override
	public boolean isElementContentWhitespace() {
		return getData().trim().length() == 0;
	}

	@Override
	public String getWholeText() {
		return getData();
	}

	@Override
	public Text replaceWholeText(String content) throws DOMException {
		throw createNoLevel3SupportException();
	}

}
