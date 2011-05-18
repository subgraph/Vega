package com.subgraph.vega.api.http.proxy;

import java.util.List;

import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public interface IHttpProxyService {
	void start(int proxyPort);
	void stop();
	IHttpProxyTransactionManipulator getTransactionManipulator();
	IHttpInterceptor getInterceptor();
	List<IResponseProcessingModule> getResponseProcessingModules();
}
