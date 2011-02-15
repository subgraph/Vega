package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptor {
	public void setRequestListener(IHttpInterceptProxyEventHandler handler);
	public void unsetRequestListener(IHttpInterceptProxyEventHandler handler);
	public void setResponseListener(IHttpInterceptProxyEventHandler handler);
	public void unsetResponseListener(IHttpInterceptProxyEventHandler handler);
	public void forwardPending();
	public void dropPending();
}
