package com.subgraph.vega.internal.model.alerts;

import com.subgraph.vega.api.model.alerts.IScanAlertHighlight;

public class ScanAlertHighlight implements IScanAlertHighlight {

	private final String matchString;
	private final boolean isRegex;
	
	ScanAlertHighlight(String matchString, boolean isRegex) {
		this.matchString = matchString;
		this.isRegex = isRegex;
	}

	@Override
	public boolean isRegularExpression() {
		return isRegex;
	}

	@Override
	public String getMatchString() {
		return matchString;
	}
}
