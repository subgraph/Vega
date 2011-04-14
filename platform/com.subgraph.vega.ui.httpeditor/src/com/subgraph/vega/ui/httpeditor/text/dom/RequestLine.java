package com.subgraph.vega.ui.httpeditor.text.dom;

import java.util.ArrayList;
import java.util.List;

public class RequestLine extends StartLine {
	public final static String ELEMENT_TYPE = "http.request-line";
	private Method method;
	private URL url;
	
	protected RequestLine(RequestModel parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
	}

	@Override
	public boolean isRequest() {
		return true;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	public Method getMethod() {
		return method;
	}
	
	public void setURL(URL url) {
		this.url = url;
	}
	
	public URL getURL() {
		return url;
	}
	
	@Override
	public Element[] getChildren() {
		final List<Element> elements = new ArrayList<Element>();
		if(method != null)
			elements.add(method);
		if(url != null)
			elements.add(url);
		if(getProtocolVersion() != null)
			elements.add(getProtocolVersion());
		return elements.toArray(new Element[0]);
	}
	
	public String toString() {
		return "Request Line";
	}
}
