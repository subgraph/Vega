package com.subgraph.vega.ui.httpeditor.dom;

public class StatusCode extends Element {
	public static final String ELEMENT_TYPE = "http.status-code";
	private final int statusCode;
	
	StatusCode(int code, ResponseLine parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
		this.statusCode = code;
	}

	public int getCode() {
		return statusCode;
	}
	
	@Override
	public Element[] getChildren() {
		return NO_CHILDREN;
	}
	
	public String toString() {
		return "Status Code "+ statusCode;
	}
}
