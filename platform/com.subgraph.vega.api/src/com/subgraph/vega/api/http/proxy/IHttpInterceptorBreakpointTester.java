package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptorBreakpointTester {
	void setMatchType(HttpInterceptorBreakpointMatchType matchType);
	void setPattern(String pattern);
	boolean test(IProxyTransaction transaction);
}
