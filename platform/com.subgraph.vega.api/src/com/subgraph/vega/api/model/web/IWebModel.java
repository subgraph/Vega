package com.subgraph.vega.api.model.web;

import java.net.URI;
import java.util.Collection;

import org.apache.http.HttpHost;

import com.subgraph.vega.api.events.IEventHandler;

public interface IWebModel {
	void addChangeListenerAndPopulate(IEventHandler listener);
	void removeChangeListener(IEventHandler listener);
	Collection<IWebHost> getAllWebHosts();
	IWebHost getWebHostByHttpHost(HttpHost host);
	IWebPath getWebPathByUri(URI uri);
	
	Collection<IWebHost> getUnscannedHosts();
	Collection<IWebPath> getUnscannedPaths();
}
