package com.subgraph.vega.api.model.web;

import java.net.InetAddress;

public interface IWebHost extends IWebEntity {
	String getHostname();
	Iterable<InetAddress> getAddresses();
	int getPort();
	IWebPath getRootPath();
	void setAddress(InetAddress address, boolean notify);
	IWebPath addPath(String path);
}
