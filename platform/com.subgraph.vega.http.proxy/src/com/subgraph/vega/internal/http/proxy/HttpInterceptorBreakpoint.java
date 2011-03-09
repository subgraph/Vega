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
			case REQUEST_HEADER:
				this.tester = new BreakpointTesterRequestHeader(this.matchType, this.pattern);
				break;

			// TODO: default
		}
	}
	
	@Override
	public synchronized HttpInterceptorBreakpointType getType() {
		return breakpointType;
	}

	@Override
	public synchronized void setMatchType(HttpInterceptorBreakpointMatchType matchType) {
		this.matchType = matchType;
		tester.setMatchType(this.matchType);
	}

	@Override
	public synchronized HttpInterceptorBreakpointMatchType getMatchType() {
		return matchType;
	}

	@Override
	public synchronized void setPattern(String pattern) {
		this.pattern = pattern;
		tester.setPattern(this.pattern);
	}

	@Override
	public synchronized String getPattern() {
		return pattern;
	}

	@Override
	public synchronized void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public synchronized boolean getIsEnabled() {
		return isEnabled;
	}

	@Override
	public synchronized boolean test(IProxyTransaction transaction) {
		if (isEnabled == true) {
			return tester.test(transaction);
		}
		return false;
	}

}
