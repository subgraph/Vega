package com.subgraph.vega.ui.http.builder;

public class BuilderParseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	BuilderParseException(String message) {
		super(message);
	}

	BuilderParseException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
