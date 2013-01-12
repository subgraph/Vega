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

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.web.IWebPath;

public abstract class AbstractRequestBuilder implements IRequestBuilder {
	
	protected final IHttpRequestEngine requestEngine;
	protected final IWebPath webPath;
		
	protected AbstractRequestBuilder(IHttpRequestEngine requestEngine, IWebPath path) {
		this.requestEngine = requestEngine;
		this.webPath = path;
	}
	
	protected HttpUriRequest createRequestForPath(String path) {
		return requestEngine.createGetRequest(webPath.getHttpHost(), path);
	}
	
	protected HttpUriRequest createPathRequest() {
		return createRequestForPath(getBasePath());
	}
	

	protected HttpUriRequest createRequestFromQuery(String query) {
		final String requestLine = getBasePath() + "?" + query;
		return createRequestForPath(requestLine);
	}

	protected String getBasePath() {
		switch(webPath.getPathType()) {
		case PATH_DIRECTORY:
			return maybeAddTrailingSlash(webPath.getFullPath());
		case PATH_PATHINFO:
			return maybeRemoveTrailingSlash(webPath.getFullPath());
		default:
			return webPath.getFullPath();
		}
	}

	private String maybeAddTrailingSlash(String basePath) {
		if(basePath.endsWith("/")) {
			return basePath;
		}
		return basePath + "/";
	}
	
	private String maybeRemoveTrailingSlash(String basePath) {
		if(!basePath.endsWith("/")) {
			return basePath;
		}
		String p = basePath;
		while(p.length() > 0 && p.endsWith("/")) {
			p = p.substring(0, p.length() - 1);
		}
		return p;
	}
}
