package com.subgraph.vega.ui.httpeditor.dom;

public class StatusReason extends Element {
	public static final String ELEMENT_TYPE = "http.status-reason";
	private final String statusReason;

	StatusReason(String reason, Element parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
		this.statusReason = reason;
	}

	public String getReason() {
		return statusReason;
	}

	@Override
	public Element[] getChildren() {
		return NO_CHILDREN;
	}
	
	public String toString() {
		return "Status reason: "+ statusReason;
	}
}
