package com.subgraph.vega.internal.model.alerts;

import com.subgraph.vega.api.model.alerts.IScanAlertHighlight;

public class ScanAlertHighlight implements IScanAlertHighlight {

	private final String matchString;
	private final boolean isRegex;
	private boolean isCaseSensitive;
	
	ScanAlertHighlight(String matchString, boolean isRegex, boolean isCaseSensitive) {
		this.matchString = matchString;
		this.isRegex = isRegex;
		this.isCaseSensitive = isCaseSensitive;
	}

	@Override
	public boolean isRegularExpression() {
		return isRegex;
	}

	@Override
	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}
	
	@Override
	public String getMatchString() {
		return matchString;
	}
}
