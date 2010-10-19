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
	
	public TextJS(Text textNode) {
		super(textNode);
		this.textNode = textNode;
	}
	
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "Text";
	}

	public Scriptable jsFunction_splitText(int offset) throws DOMException {
		final Text txt = textNode.splitText(offset);
		if(txt == null)
			return null;
		else {
			NodeJS textNode = new TextJS(txt);
			exportObject(textNode);
			return textNode;
		}
	}

}
