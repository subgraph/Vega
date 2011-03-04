package com.subgraph.vega.internal.http.proxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpointTester;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;

public class BreakpointTesterRequestType implements IHttpInterceptorBreakpointTester {
	private HttpInterceptorBreakpointMatchType matchType;
	Pattern pattern;

	BreakpointTesterRequestType(HttpInterceptorBreakpointMatchType matchType, String pattern) {
		this.matchType = matchType;
		this.pattern = Pattern.compile(pattern);
	}

	@Override
	public boolean test(IProxyTransaction transaction) {
		HttpRequest request = transaction.getRequest();
		if (request != null) {
			Matcher matcher = pattern.matcher(request.getRequestLine().getMethod());
			if (matcher.find() == (matchType == HttpInterceptorBreakpointMatchType.MATCH)) {
				return true;
			}
		}
		return false;
	}

}
