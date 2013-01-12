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

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.web.IWebPath;

public class PostParameterRequestBuilder extends AbstractParameterRequestBuilder {

	public PostParameterRequestBuilder(IHttpRequestEngine requestEngine, IWebPath path, List<NameValuePair> parameters, int index) {
		super(requestEngine, path, parameters, index);
	}

	@Override
	public HttpUriRequest createBasicRequest() {
		return createPostRequest(parameters);
	}

	private HttpUriRequest createPostRequest(List<NameValuePair> parameters) {
		final HttpUriRequest req =  requestEngine.createPostRequest(webPath.getHttpHost(), getBasePath());
		((HttpEntityEnclosingRequest)req).setEntity(createParameterEntity(parameters));
		return req;
	}

	@Override
	public HttpUriRequest createAlteredRequest(String value, boolean append) {
		return createPostRequest(getAlteredParameters(value, append, false));
	}

	@Override
	public HttpUriRequest createAlteredParameterNameRequest(String name) {
		return createPostRequest(getAlteredParameters(name, false, true));
	}
	
	private HttpEntity createParameterEntity(List<NameValuePair> parameters) {
		try {
			return new UrlEncodedFormEntity(parameters, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to encode form parameters.", e);
		}
	}
	
	@Override
	public String toString() {
		return "POST "+ getBasePath() + " ("+ printParameters() + ") (idx = "+ parameterFuzzIndex + ")";
	}
	
	private String printParameters() {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(NameValuePair p: parameters) {
			if(first)
				first = false;
			else
				sb.append(", ");
			sb.append(p.getName());
			if(p.getValue() != null) {
				sb.append("=");
				sb.append(p.getValue());
			}
		}
		return sb.toString();
	}
}
