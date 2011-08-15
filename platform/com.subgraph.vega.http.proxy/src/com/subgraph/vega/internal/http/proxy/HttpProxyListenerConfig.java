package com.subgraph.vega.internal.http.proxy;

import java.net.InetAddress;

import com.subgraph.vega.api.http.proxy.IHttpProxyListenerConfig;

public class HttpProxyListenerConfig implements IHttpProxyListenerConfig {
	private InetAddress inetAddress;
	private int port;
	private int backlog = 10;

	@Override
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	@Override
	public void setInetAddress(InetAddress address) {
		this.inetAddress = address;		
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int getBacklog() {
		return backlog;
	}

	@Override
	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	@Override
	public String getListenerAddress() {
		return "[" + inetAddress.getHostAddress() + "]:" + port;
	}

	@Override
	public String toString() {
		return getListenerAddress();
	}

}
