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

import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;

import com.db4o.ObjectContainer;
import com.db4o.activation.ActivationPurpose;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebModelVisitor;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;

public class WebResponse extends WebEntity implements IWebResponse {

	private final IWebPath parentPath;
	private final List<NameValuePair> parameters;
	private final String mimeType;
	
	private transient String cachedQueryString;
	
	WebResponse(EventListenerManager eventManager, ObjectContainer database, IWebPath parentPath, List<NameValuePair> parameters, String mimeType) {
		super(eventManager, database);
		this.parentPath = parentPath;
		this.mimeType = mimeType;
		this.parameters = parameters;
		setVisited(true);
	}
	
	
	@Override
	public List<NameValuePair> getRequestParameters() {
		activate(ActivationPurpose.READ);
		return Collections.unmodifiableList(parameters);
	}

	@Override
	public String getMimeType() {
		activate(ActivationPurpose.READ);
		return mimeType;
	}


	@Override
	public IWebEntity getParent() {
		activate(ActivationPurpose.READ);
		return parentPath;
	}


	@Override
	public String getQueryString() {
		activate(ActivationPurpose.READ);
		synchronized(this) {
			if(cachedQueryString == null) {
				cachedQueryString = createQueryString();
			}
			return cachedQueryString;
		}
	}
	
	private String createQueryString() {
		final StringBuilder sb = new StringBuilder();
		for(NameValuePair pair: parameters) {
			if(sb.length() > 0)
				sb.append("&");
			sb.append(pair.getName());
			if(pair.getValue() != null) {
				sb.append("=");
				sb.append(pair.getValue());
			}
		}
		return sb.toString();
	}


	@Override
	public IWebPath getPathEntity() {
		activate(ActivationPurpose.READ);
		return parentPath;
	}


	@Override
	public void accept(IWebModelVisitor visitor) {
		visitor.visit(this);
	}


	@Override
	public HttpHost getHttpHost() {
		return parentPath.getHttpHost();
	}
}
