package com.subgraph.vega.api.model.web;

import java.net.URI;

import org.apache.http.HttpHost;

public interface IWebHost extends IWebEntity {
	URI getUri();
	HttpHost getHttpHost();
	String getScheme();
	String getHostname();
	int getPort();
	IWebMountPoint getRootMountPoint();
	IWebPath getRootPath();
	IWebPath addPath(String path);
}
