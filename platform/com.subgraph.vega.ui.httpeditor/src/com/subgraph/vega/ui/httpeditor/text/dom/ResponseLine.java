package com.subgraph.vega.ui.httpeditor.text.dom;

public class ResponseLine extends StartLine {
	public static final String ELEMENT_TYPE = "http.response-line";
	private StatusCode statusCode;
	private StatusReason statusReason;
	
	protected ResponseLine(RequestModel parent, int offset, int length) {
		super(ELEMENT_TYPE, parent, offset, length);
	}

	@Override
	public boolean isRequest() {
		return false;
	}

	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}
	
	public StatusCode getStatusCode() {
		return statusCode;
	}
	
	public void setStatusReason(StatusReason reason) {
		this.statusReason = reason;
	}
	public StatusReason getStatusReason() {
		return statusReason;
	}
	
	@Override
	public Element[] getChildren() {
		return new Element[] { getProtocolVersion(), statusCode, statusReason };
	}
	
	public String toString() {
		return "Response Line";
	}
}