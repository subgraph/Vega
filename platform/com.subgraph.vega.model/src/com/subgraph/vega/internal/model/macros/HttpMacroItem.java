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
package com.subgraph.vega.internal.model.macros;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.macros.IHttpMacroItem;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class HttpMacroItem implements IHttpMacroItem, Activatable {
	private transient Activator activator;
	private IRequestLogRecord requestLogRecord;
	private boolean useCookies;
	private boolean keepCookies;
	private ActivatableHashMap<String, IHttpMacroItemParam> paramDict;

	public HttpMacroItem(IRequestLogRecord requestLogRecord) {
		this.requestLogRecord = requestLogRecord;
		useCookies = true;
		keepCookies = true;
		paramDict = new ActivatableHashMap<String, IHttpMacroItemParam>();
		createParams();
	}

	@Override
	public IRequestLogRecord getRequestLogRecord() {
		activate(ActivationPurpose.READ);
		return requestLogRecord;
	}

	@Override
	public void setUseCookies(boolean useCookies) {
		activate(ActivationPurpose.READ);
		this.useCookies = useCookies;
		activate(ActivationPurpose.WRITE);

	}

	@Override
	public boolean getUseCookies() {
		activate(ActivationPurpose.READ);
		return useCookies;
	}

	@Override
	public void setKeepCookies(boolean keepCookies) {
		activate(ActivationPurpose.READ);
		this.keepCookies = keepCookies;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public boolean getKeepCookies() {
		activate(ActivationPurpose.READ);
		return keepCookies;
	}

	@Override
	public Collection<IHttpMacroItemParam> getParams() {
		activate(ActivationPurpose.READ);
		return paramDict.values();
	}

	@Override
	public IHttpMacroItemParam getParam(String name) {
		activate(ActivationPurpose.READ);
		return paramDict.get(name);
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if (activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if (this.activator == activator) {
			return;
		}
		if (activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		this.activator = activator;			
	}

	// XXX clean this
	private void createParams() {
		HttpRequest request = requestLogRecord.getRequest();

		URI uri = null;
		try {
			uri = new URI(request.getRequestLine().getUri());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<NameValuePair> requestParamList = URLEncodedUtils.parse(uri, "UTF-8");
		for (NameValuePair pair: requestParamList) {
			paramDict.put(pair.getName(), new HttpMacroItemParam(pair.getName(), pair.getValue()));
		}		

		if (request instanceof HttpEntityEnclosingRequest) {
			List<NameValuePair> entityParamList = null;
			try {
				entityParamList = URLEncodedUtils.parse(((HttpEntityEnclosingRequest) request).getEntity());
			} catch (IOException e) {
				// there won't be any exceptions for a record from the database
			}
			for (NameValuePair pair: entityParamList) {
				paramDict.put(pair.getName(), new HttpMacroItemParam(pair.getName(), pair.getValue()));
			}
		}
	}	

}
