package com.subgraph.vega.api.model.web.old;

public interface IWebHost extends IWebEntity {
	String getHostname();
	int getPort();
	IWebPath getRootPath();
	IWebPath addPath(String path);
}
