package com.subgraph.vega.http.proxy;

public interface IHttpInterceptProxyEventHandler {
	void handleRequest(IProxyTransaction transaction);
}
