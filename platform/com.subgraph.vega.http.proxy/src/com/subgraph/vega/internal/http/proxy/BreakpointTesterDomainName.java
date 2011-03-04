package com.subgraph.vega.internal.http.proxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;

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
	public boolean test(IProxyTransaction transaction) {
		HttpHost httpHost = transaction.getHttpHost();
		if (httpHost != null) {
			Matcher matcher = pattern.matcher(httpHost.getHostName());
			if (matcher.find() == (matchType == HttpInterceptorBreakpointMatchType.MATCH)) {
				return true;
			}
		}
		return false;
	}

}
