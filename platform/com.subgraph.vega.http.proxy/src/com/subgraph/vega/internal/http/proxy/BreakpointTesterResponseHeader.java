package com.subgraph.vega.internal.http.proxy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpointTester;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;

public class BreakpointTesterResponseHeader implements IHttpInterceptorBreakpointTester {
	private HttpInterceptorBreakpointMatchType matchType;
	Pattern pattern;

	BreakpointTesterResponseHeader(HttpInterceptorBreakpointMatchType matchType, String pattern) {
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
		HttpResponse response = transaction.getResponse().getRawResponse();
		if (response != null) {
			for (HeaderIterator iterator = response.headerIterator(); iterator.hasNext();) {
				Header header = (Header) iterator.next();
				Matcher matcher = pattern.matcher(header.toString());
				if (matcher.find() == (matchType == HttpInterceptorBreakpointMatchType.MATCH)) {
					return true;
				}
			}
		}
		return false;
	}

}
