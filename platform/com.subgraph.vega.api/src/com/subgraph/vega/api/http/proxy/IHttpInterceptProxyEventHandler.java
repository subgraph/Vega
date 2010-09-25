package com.subgraph.vega.api.http.proxy;

public interface IHttpInterceptProxyEventHandler {
	void handleRequest(IProxyTransaction transaction);
}
