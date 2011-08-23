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
package com.subgraph.vega.internal.model.web;

import org.apache.http.HttpHost;

import com.db4o.ObjectContainer;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebMountPoint;


public class WebMountPoint implements IWebMountPoint, Activatable {

	static WebMountPoint createRootMountPoint(EventListenerManager eventManager, ObjectContainer database, HttpHost httpHost) {
		WebPath rootPath = WebPath.createRootPath(eventManager, database);
		final WebMountPoint mountPoint = new WebMountPoint(rootPath);
		rootPath.setMountPoint(mountPoint);
		return mountPoint;
	}
	private transient Activator activator;

	private IWebHost host;
	private final WebPath path;
	
	WebMountPoint() {
		this.path = null;
		this.host = null;
	}
	
	
	private WebMountPoint(WebPath rootPath) {
		this.path = rootPath;
		this.host = null;
	}
	
	WebMountPoint(IWebHost host, WebPath path) {
		this.host = host;
		this.path = path;
	}

	@Override
	public IWebHost getWebHost() {
		activate(ActivationPurpose.READ);
		return host;
	}

	@Override
	public WebPath getMountPath() {
		activate(ActivationPurpose.READ);
		return path;
	}
	
	void setWebHost(IWebHost host) {
		activate(ActivationPurpose.READ);
		this.host = host;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}		
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;		
	}

}
