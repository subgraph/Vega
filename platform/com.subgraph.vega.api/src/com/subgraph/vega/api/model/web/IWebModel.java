/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.api.model.web;

import java.util.Collection;

import org.apache.http.HttpHost;
import org.w3c.dom.html2.HTMLDocument;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.web.forms.IWebForm;
import com.subgraph.vega.api.util.VegaURI;

public interface IWebModel extends IWebModelVisitable {
	void addChangeListenerAndPopulate(IEventHandler listener);
	void removeChangeListener(IEventHandler listener);
	Collection<IWebHost> getAllWebHosts();
	IWebHost getWebHostByHttpHost(HttpHost host);
	IWebHost createWebHostFromHttpHost(HttpHost host);
	IWebPath getWebPathByUri(VegaURI uri);
	
	IWebPath addGetTarget(VegaURI uri);

	Collection<IWebHost> getUnscannedHosts();
	Collection<IWebPath> getUnscannedPaths();
	Collection<IWebPath> getAllPaths();
	
	Collection<IWebForm> parseForms(IWebPath source, HTMLDocument document);
}
