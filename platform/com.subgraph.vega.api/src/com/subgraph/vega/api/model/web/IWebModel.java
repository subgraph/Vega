package com.subgraph.vega.api.model.web;

import java.net.InetAddress;
import java.net.URI;

import com.subgraph.vega.api.events.IEventHandler;

public interface IWebModel {
	
	void addChangeListenerAndPopulate(IEventHandler listener);
	void removeChangeListener(IEventHandler listener);
	
	IWebHost getWebHostByNameAndPort(String name, int port);
	IWebHost addWebHost(String name, InetAddress address, int port, boolean isSSL);
	IWebPath addURI(URI uri);
}
