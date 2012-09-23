package com.subgraph.vega.api.model.alerts;

public interface IScanAlertHighlight {
	boolean isRegularExpression();
	boolean isCaseSensitive();
	String getMatchString();
}
