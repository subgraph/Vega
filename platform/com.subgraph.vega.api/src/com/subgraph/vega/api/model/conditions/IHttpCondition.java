package com.subgraph.vega.api.model.conditions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IHttpCondition {
	enum MatchOption {
		DOES_MATCH("matches", false),
		DOESNT_MATCH("does not match", true);
	
		private final String name;
		private final boolean inverted;
		MatchOption(String name, boolean invert) { this.name = name; this.inverted = invert; }
		public String getName() { return name; }
		public boolean getInverted() { return inverted; }
	};
	
	IHttpConditionType getType();
	IHttpCondition createCopy();
	boolean matches(HttpRequest request);
	boolean matches(HttpResponse response);
	boolean matches(HttpRequest request, HttpResponse response);
	boolean isEnabled();
	void setEnabled(boolean state);
	/*
	 * Changes the meaning of matches() to 'does not match' condition.
	 */
	boolean isInverted();
	void setInverted(boolean flag);
	MatchOption getMatchOption();
}
