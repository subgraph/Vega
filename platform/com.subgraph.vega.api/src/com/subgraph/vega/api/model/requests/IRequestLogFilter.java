package com.subgraph.vega.api.model.requests;

public interface IRequestLogFilter {
	boolean match(IRequestLogRecord record);
}
