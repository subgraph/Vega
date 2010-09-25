package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptProxy {
	void startProxy();
	void stopProxy();
	void registerEventHandler(IHttpInterceptProxyEventHandler handler);
	void unregisterEventHandler(IHttpInterceptProxyEventHandler handler);
}
