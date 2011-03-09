package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptor {
	void setEventHandler(IHttpInterceptorEventHandler eventHandler);
	void setInterceptLevel(ProxyTransactionDirection direction, HttpInterceptorLevel level);
	HttpInterceptorLevel getInterceptLevel(ProxyTransactionDirection direction);
	IHttpInterceptorBreakpoint createBreakpoint(ProxyTransactionDirection direction, HttpInterceptorBreakpointType breakpointType, HttpInterceptorBreakpointMatchType matchType, String pattern, boolean isEnabled);
	void removeBreakpoint(ProxyTransactionDirection direction, IHttpInterceptorBreakpoint breakpoint);
	int getBreakpontIdxOf(ProxyTransactionDirection direction, IHttpInterceptorBreakpoint breakpoint);
	int getBreakpointCnt(ProxyTransactionDirection direction);
	IHttpInterceptorBreakpoint[] getBreakpoints(ProxyTransactionDirection direction);
	int transactionQueueSize();
	IProxyTransaction transactionQueuePop();
}
