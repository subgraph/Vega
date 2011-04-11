package com.subgraph.vega.ui.httpeditor.text.dom;

public abstract class Element {
	protected static Element[] NO_CHILDREN = new Element[0];
	private final String name;
	private final int offset;
	private final int length;
	private final Element parent;
	
	Element(String name, Element parent, int offset, int length) {
		this.name = name;
		this.parent = parent;
		this.offset = offset;
		this.length = length;
	}
	
	public abstract Element[] getChildren();
	
	public Element findElementAtOffset(int off) {
		if(!elementContainsOffset(off))
			return null;
		for(Element e: getChildren()) {
			Element e2 = e.findElementAtOffset(off);
			if(e2 != null)
				return e2;
		}
		return this;
	}
	
	public boolean elementContainsOffset(int off) {
		return (off >= offset) && (off < (offset + length));
	}
	
	public Element getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getLength() {
		return length;
	}
}