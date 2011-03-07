package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptor {
	void setEventHandler(IHttpInterceptorEventHandler eventHandler);
	IHttpInterceptorBreakpoint createBreakpoint(ProxyTransactionDirection direction, HttpInterceptorBreakpointType breakpointType, HttpInterceptorBreakpointMatchType matchType, String pattern, boolean isEnabled);
	void removeBreakpoint(ProxyTransactionDirection direction, IHttpInterceptorBreakpoint breakpoint);
	int getBreakpontIdxOf(ProxyTransactionDirection direction, IHttpInterceptorBreakpoint breakpoint);
	int getBreakpointCnt(ProxyTransactionDirection direction);
	IHttpInterceptorBreakpoint[] getBreakpoints(ProxyTransactionDirection direction);
	int transactionQueueSize();
	IProxyTransaction transactionQueuePop();
}
