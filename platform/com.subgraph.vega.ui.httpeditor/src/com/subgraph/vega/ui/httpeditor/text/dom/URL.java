package com.subgraph.vega.ui.httpeditor.text.dom;

public class URL extends Element {
	public final static String ELEMENT_TYPE = "http.url";
	private final String urlText;
	URL(String urlText, RequestLine parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
		this.urlText = urlText;
	}

	public String getUrlText() {
		return urlText;
	}
	
	@Override
	public Element[] getChildren() {
		return NO_CHILDREN;
	}
	
	public String toString() {
		return "URL: "+ urlText;
	}
}