package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptor {
	public void setRequestListener(IHttpInterceptProxyEventHandler handler);
	public void unsetRequestListener(IHttpInterceptProxyEventHandler handler);
	public void setResponseListener(IHttpInterceptProxyEventHandler handler);
	public void unsetResponseListener(IHttpInterceptProxyEventHandler handler);
	public IHttpInterceptorBreakpoint createBreakpoint(HttpInterceptorBreakpointType breakpointType, HttpInterceptorBreakpointMatchType matchType, String pattern, boolean isEnabled);
	public void removeBreakpoint(IHttpInterceptorBreakpoint breakpoint);
	public int getBreakpontIdxOf(IHttpInterceptorBreakpoint breakpoint);
	public int getBreakpointCnt();
	public IHttpInterceptorBreakpoint[] getBreakpoints();
	public void forwardPending();
	public void dropPending();
}
