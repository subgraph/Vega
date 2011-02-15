package com.subgraph.vega.api.model.web;

import java.net.URI;
import java.util.Collection;

import org.apache.http.HttpHost;
import org.w3c.dom.html2.HTMLDocument;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.web.forms.IWebForm;

public interface IWebModel {
	void addChangeListenerAndPopulate(IEventHandler listener);
	void removeChangeListener(IEventHandler listener);
	Collection<IWebHost> getAllWebHosts();
	IWebHost getWebHostByHttpHost(HttpHost host);
	IWebPath getWebPathByUri(URI uri);
	
	IWebPath addGetTarget(URI uri);

	Collection<IWebHost> getUnscannedHosts();
	Collection<IWebPath> getUnscannedPaths();
	Collection<IWebPath> getAllPaths();
	
	Collection<IWebForm> parseForms(IWebPath source, HTMLDocument document);
}
