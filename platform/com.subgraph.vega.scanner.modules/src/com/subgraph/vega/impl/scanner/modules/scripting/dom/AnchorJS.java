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
