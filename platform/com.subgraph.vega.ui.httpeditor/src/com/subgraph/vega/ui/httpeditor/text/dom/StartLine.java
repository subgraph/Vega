package com.subgraph.vega.ui.httpeditor.text.dom;

public abstract class StartLine extends Element {

	private ProtocolVersion protocolVersion;
	protected StartLine(String name, RequestModel parent, int offset, int length) {
		super(name, parent, offset, length);
	}

	public void setProtocolVersion(ProtocolVersion version) {
		this.protocolVersion = version;
	}
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}
	
	abstract public boolean isRequest();
}