package com.subgraph.vega.api.model.alerts;

public interface IScanAlertHighlight {
	boolean isRegularExpression();
	String getMatchString();
}
