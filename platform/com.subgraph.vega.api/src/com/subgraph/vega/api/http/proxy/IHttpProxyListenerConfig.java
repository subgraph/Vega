package com.subgraph.vega.api.http.proxy;

import java.net.InetAddress;

public interface IHttpProxyListenerConfig {
	InetAddress getInetAddress();
	void setInetAddress(InetAddress address);
	int getPort();
	void setPort(int port);
	int getBacklog();
	void setBacklog(int backlog);
	String getListenerAddress();
}
