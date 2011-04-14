package com.subgraph.vega.ui.httpeditor.text.dom;

import java.util.ArrayList;
import java.util.List;

public class RequestModel extends Element {
	public final static String ELEMENT_TYPE = "http.request";
	private StartLine startLine;
	private List<Header> headers;
	
	RequestModel(int length) {
		super(ELEMENT_TYPE, null, 0, length);
		headers = new ArrayList<Header>();
	}
	
	public void setStartLine(StartLine startLine) {
		this.startLine = startLine;
	}
	
	public StartLine getStartLine() {
		return startLine;
	}
	
	public void addHeader(Header h) {
		headers.add(h);
	}
	
	public Header[] getHeaders() {
		return headers.toArray(new Header[0]);
	}
	
	@Override
	public Element[] getChildren() {
		final Header[] headers = getHeaders();
		final Element[] children = new Element[headers.length + 1];
		children[0] = startLine;
		System.arraycopy(headers, 0, children, 1, headers.length);
		return children;
	}
	
	public String toString() {
		return "Request Model";
	}
	
	public void printModel() {
		printElement(this, 0);
	}
	
	private static void printElement(Element root, int depth) {
		printSpaces(depth * 2);
		System.out.println(root);
		for(Element e : root.getChildren()) 
			printElement(e, depth + 1);
	}
	
	private static void printSpaces(int count) {
		for(int i = 0; i < count; i++)
			System.out.print(' ');
	}

}