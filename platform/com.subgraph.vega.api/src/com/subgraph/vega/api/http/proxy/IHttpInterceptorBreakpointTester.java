package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptorBreakpointTester {
	boolean test(IProxyTransaction transaction);
}
