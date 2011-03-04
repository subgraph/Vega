package com.subgraph.vega.internal.http.proxy;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpoint;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpointTester;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;

public class HttpInterceptorBreakpoint implements IHttpInterceptorBreakpoint {
	final private HttpInterceptorBreakpointType breakpointType;
	private HttpInterceptorBreakpointMatchType matchType;
	private String pattern;
	private boolean isEnabled;
	private IHttpInterceptorBreakpointTester tester;

	public HttpInterceptorBreakpoint(HttpInterceptorBreakpointType breakpointType, HttpInterceptorBreakpointMatchType matchType, String pattern, boolean isEnabled) {
		this.breakpointType = breakpointType;
		this.matchType = matchType;
		this.pattern = pattern;
		this.isEnabled = isEnabled;

		switch (this.breakpointType) {
			case DOMAIN_NAME:
				this.tester = new BreakpointTesterDomainName(this.matchType, this.pattern);
				break;
			case REQUEST_METHOD:
				this.tester = new BreakpointTesterRequestType(this.matchType, this.pattern);
				break;
			case HEADER:
				this.tester = new BreakpointTesterHeader(this.matchType, this.pattern);
				break;

			// TODO: default
		}
	}
	
	@Override
	public HttpInterceptorBreakpointType getType() {
		return breakpointType;
	}

	@Override
	public void setMatchType(HttpInterceptorBreakpointMatchType matchType) {
		this.matchType = matchType;
	}

	@Override
	public HttpInterceptorBreakpointMatchType getMatchType() {
		return matchType;
	}

	@Override
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return isEnabled;
	}

	@Override
	public boolean test(IProxyTransaction transaction) {
		return tester.test(transaction);
	}

}
