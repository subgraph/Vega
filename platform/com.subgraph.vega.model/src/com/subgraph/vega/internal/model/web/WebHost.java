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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;

import com.db4o.ObjectContainer;
import com.db4o.activation.ActivationPurpose;
import com.google.common.base.Objects;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModelVisitor;
import com.subgraph.vega.api.model.web.IWebMountPoint;


public class WebHost extends WebEntity implements IWebHost {
	
	static WebHost createWebHost(EventListenerManager eventManager, ObjectContainer database, HttpHost host) {
		final WebMountPoint rootMountPoint = WebMountPoint.createRootMountPoint(eventManager, database, checkNotNull(host));
		final WebHost webHost =  new WebHost(eventManager, database, host, rootMountPoint);
		rootMountPoint.setWebHost(webHost);
		return webHost;
	}

	private final HttpHost host;
	private final WebMountPoint mountPoint;
	private final WebPath rootPath;
	
	private transient URI cachedUri;
	
	
	private WebHost(EventListenerManager eventManager, ObjectContainer database, HttpHost host, WebMountPoint rootMountPoint) {
		super(eventManager, database);
		if(host.getHostName().isEmpty()) {
			throw new IllegalArgumentException("Hostname is empty");
		}
		hostToUri(host);
		this.host = host;
		this.mountPoint = rootMountPoint;
		this.rootPath = rootMountPoint.getMountPath();
	}
	
	@Override
	public HttpHost getHttpHost() {
		activate(ActivationPurpose.READ);
		return host;
	}

	@Override
	public String getScheme() {
		return getHttpHost().getSchemeName();
	}

	@Override
	public String getHostname() {
		return getHttpHost().getHostName();
	}

	@Override
	public int getPort() {
		return getHttpHost().getPort();
	}

	@Override
	public IWebMountPoint getRootMountPoint() {
		activate(ActivationPurpose.READ);
		return mountPoint;
	}

	@Override
	public WebPath getRootPath() {
		activate(ActivationPurpose.READ);
		return rootPath;
	}

	@Override
	public URI getUri() {
		activate(ActivationPurpose.READ);
		synchronized(this) {
			if(cachedUri == null)
				cachedUri = generateUri();
			return cachedUri;
		}
	}
	
	private static URI hostToUri(HttpHost host) {
		try {
			return new URI(host.getSchemeName(), null, host.getHostName(), host.getPort(), null, null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Unable to convert host to URI: "+ host);
		}
	}

	private URI generateUri() {
		return hostToUri(host);
	}

	@Override
	public WebPath addPath(String path) {
		activate(ActivationPurpose.READ);
		WebPath wp = getRootPath();
		for(String s: path.split("/")) {
			if(!s.isEmpty()) {
				wp = wp.addChildPath(s);
			}
		}
		return wp;		
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(this == other)
			return true;
		else if(other instanceof WebHost) {
			WebHost that = (WebHost) other;
			return this.getHttpHost().equals(that.getHttpHost());
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return getHttpHost().hashCode();
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("host", getHttpHost()).toString();
	}

	@Override
	public IWebEntity getParent() {
		activate(ActivationPurpose.READ);
		return null;
	}

	@Override
	public void accept(IWebModelVisitor visitor) {
		visitor.visit(this);
		getRootPath().accept(visitor);
	}
}
