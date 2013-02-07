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
package com.subgraph.vega.impl.scanner.requests;


import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.web.IWebPath;

public class BasicRequestBuilder extends AbstractRequestBuilder {

	public BasicRequestBuilder(IHttpRequestEngine requestEngine, IWebPath path) {
		super(requestEngine, path);
	}

	@Override
	public HttpUriRequest createBasicRequest() {
		return createPathRequest();
	}

	@Override
	public HttpUriRequest createAlteredRequest(String value, boolean append) {
		final String path = createPathWithSuffix(getBasePath(), value);
		return createRequestForPath(path);
	}

	private String createPathWithSuffix(String oldPath, String suffix) {
		if(oldPath.endsWith("/") && suffix.startsWith("/"))
			return oldPath + suffix.substring(1);
		else if(oldPath.endsWith("/") || suffix.startsWith("/"))
			return oldPath + suffix;
		else
			return oldPath + "/" + suffix;
	}

	@Override
	public HttpUriRequest createAlteredParameterNameRequest(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameValuePair getFuzzableParameter() {
		return new BasicNameValuePair("", "");
	}

	@Override
	public boolean isFuzzable() {
		return false;
	}
	
	@Override
	public String toString() {
		return "GET "+ getBasePath();
	}
}
