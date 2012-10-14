package com.subgraph.vega.internal.model.scope;

public class TargetScopeId {
	private long currentId = 0;
	
	synchronized long allocateId() {
		final long ret = currentId;
		currentId ++;
		return ret;
	}
}
