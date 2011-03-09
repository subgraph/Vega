package com.subgraph.vega.internal.http.proxy;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpointTester;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;

public class BreakpointTesterDomainName implements IHttpInterceptorBreakpointTester {
	private HttpInterceptorBreakpointMatchType matchType;
	Pattern pattern;

	BreakpointTesterDomainName(HttpInterceptorBreakpointMatchType matchType, String pattern) {
		this.matchType = matchType;
		this.pattern = Pattern.compile(pattern);
	}
	
	@Override
	public void setMatchType(HttpInterceptorBreakpointMatchType matchType) {
		this.matchType = matchType;
	}

	@Override
	public void setPattern(String pattern) {
		this.pattern = Pattern.compile(pattern);		
	}

	@Override
	public boolean test(IProxyTransaction transaction) {
		URI uri = transaction.getUri();
		if (uri != null) {
			Matcher matcher = pattern.matcher(uri.getHost());
			if (matcher.find() == (matchType == HttpInterceptorBreakpointMatchType.MATCH)) {
				return true;
			}
		}
		return false;
	}

}
