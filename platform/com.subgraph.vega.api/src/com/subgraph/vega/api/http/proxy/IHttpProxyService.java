package com.subgraph.vega.api.http.proxy;

import java.util.List;

import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public interface IHttpProxyService {
	boolean isRunning();
	boolean isPassthrough();
	void start(int proxyPort);
	void stop();
	void setPassthrough(boolean enabled);
	int getListenPort();
	IHttpProxyTransactionManipulator getTransactionManipulator();
	IHttpInterceptor getInterceptor();
	List<IResponseProcessingModule> getResponseProcessingModules();
}
