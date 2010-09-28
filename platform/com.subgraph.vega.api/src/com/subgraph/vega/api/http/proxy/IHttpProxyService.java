package com.subgraph.vega.api.http.proxy;

public interface IHttpProxyService {
	void start(int proxyPort);
	void stop();
}
