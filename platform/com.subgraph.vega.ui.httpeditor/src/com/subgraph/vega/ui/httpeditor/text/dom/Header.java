package com.subgraph.vega.ui.httpeditor.text.dom;

public class Header extends Element {
	public static final String ELEMENT_TYPE = "http.header";

	private final String headerName;
	private Element headerValue;
	
	Header(String headerName, RequestModel parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
		this.headerName = headerName;
	}

	public String getHeaderName() {
		return headerName;
	}
	
	public void setHeaderValue(Element value) {
		this.headerValue = value;
	}
	public Element getHeaderValue() {
		return headerValue;
	}
	
	@Override
	public Element[] getChildren() {
		return new Element[] { headerValue };
	}
	
	public String toString() {
		return "Header name "+ headerName;
	}
}
