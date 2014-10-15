package com.subgraph.vega.internal.sslprobe;

public class TLSAlertException extends Exception {
	private static final long serialVersionUID = 1L;
	private final int level;
	private final int description;
	
	public TLSAlertException(int level, int description) {
		this.level = level;
		this.description = description;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getDescription() {
		return description;
	}
	
	public String toString() {
		return "TLSAlertException("+ level + ", "+ description + ")";
	}
}
