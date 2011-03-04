package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptorBreakpoint {
	public HttpInterceptorBreakpointType getType();
	public void setMatchType(HttpInterceptorBreakpointMatchType matchType);
	public HttpInterceptorBreakpointMatchType getMatchType();
	public void setPattern(String pattern);
	public String getPattern();
	public void setIsEnabled(boolean isEnabled);
	public boolean getIsEnabled();
	public boolean test(IProxyTransaction transaction);
}
