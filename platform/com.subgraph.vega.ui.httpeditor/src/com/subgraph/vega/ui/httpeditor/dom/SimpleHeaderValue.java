package com.subgraph.vega.ui.httpeditor.dom;

public class SimpleHeaderValue extends Element {
	public final static String ELEMENT_TYPE = "http.simple-header-value";
	private final String headerValue;
	SimpleHeaderValue(String headerValue, Header parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
		this.headerValue = headerValue;
	}

	public String getValue() {
		return headerValue;
	}
	
	@Override
	public Element[] getChildren() {
		return NO_CHILDREN;
	}
	
	public String toString() {
		return "Simple Header value: "+ headerValue;
	}
}
