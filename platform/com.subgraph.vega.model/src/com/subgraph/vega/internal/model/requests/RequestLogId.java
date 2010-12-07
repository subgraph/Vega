package com.subgraph.vega.internal.model.requests;

public class RequestLogId {
	private long currentId = 0;
	
	synchronized long allocateId() {
		final long ret = currentId;
		currentId++;
		return ret;
	}
}
