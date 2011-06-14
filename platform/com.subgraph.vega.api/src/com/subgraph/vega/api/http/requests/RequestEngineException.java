package com.subgraph.vega.api.http.requests;

public class RequestEngineException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RequestEngineException(String message) {
		super(message);
	}
	
	public RequestEngineException(String message, Throwable ex) {
		super(message, ex);
	}
}
