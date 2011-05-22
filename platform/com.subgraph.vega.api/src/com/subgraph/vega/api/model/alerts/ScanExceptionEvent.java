package com.subgraph.vega.api.model.alerts;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.events.IEvent;

public class ScanExceptionEvent implements IEvent {
	
	private final HttpUriRequest request;
	private final Throwable exception;
	
	
	public ScanExceptionEvent(HttpUriRequest request, Throwable exception) {
		this.request = request;
		this.exception = exception;
	}
	
	public HttpUriRequest getRequest() {
		return request;
	}
	
	public Throwable getException() {
		return exception;
	}
}
