package com.subgraph.vega.internal.html.dom;

import org.jsoup.nodes.TextNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

public class TextImpl extends CharacterDataImpl implements Text {

	
	TextImpl(TextNode textNode, Document ownerDocument) {
		super(textNode, textNode.getWholeText(), ownerDocument);
	}
	
	
	@Override
	public Text splitText(int offset) throws DOMException {
		throw createReadOnlyException();
	}

	@Override
	public boolean isElementContentWhitespace() {
		throw createNoLevel3SupportException();
	}

	@Override
	public String getWholeText() {
		throw createNoLevel3SupportException();
	}

	@Override
	public Text replaceWholeText(String content) throws DOMException {
		throw createNoLevel3SupportException();
	}

}
