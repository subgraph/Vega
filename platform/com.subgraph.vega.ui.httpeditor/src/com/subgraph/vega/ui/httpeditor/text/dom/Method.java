package com.subgraph.vega.ui.httpeditor.text.dom;

public class Method extends Element {
	public final static String ELEMENT_TYPE = "http.method";
	private final String methodVerb;
	Method(String method, StartLine parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
		this.methodVerb = method;
	}

	public String getMethodVerb() {
		return methodVerb;
	}
	
	@Override
	public Element[] getChildren() {
		return NO_CHILDREN;
	}
	
	public String toString() {
		return "Method "+ methodVerb;
	}
}