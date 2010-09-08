package com.subgraph.vega.internal.requestlog;

public class RequestLogId {
	private long currentId = 0;
	
	synchronized long allocateId() {
		final long ret = currentId;
		currentId++;
		return ret;
	}

}
