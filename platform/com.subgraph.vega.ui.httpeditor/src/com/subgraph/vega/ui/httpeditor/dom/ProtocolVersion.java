package com.subgraph.vega.ui.httpeditor.dom;

public class ProtocolVersion extends Element {
	public final static String ELEMENT_TYPE = "http.protocol-version";
	private final int majorVersion;
	private final int minorVersion;
	ProtocolVersion(int major, int minor, StartLine parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
		this.majorVersion = major;
		this.minorVersion = minor;
	}

	public int getMajorVersion() {
		return majorVersion;
	}
	
	public int getMinorVersion() {
		return minorVersion;
	}
	
	@Override
	public Element[] getChildren() {
		return NO_CHILDREN;
	}
	
	public String toString() {
		return "Protocol "+ majorVersion +"."+ minorVersion;
	}
}