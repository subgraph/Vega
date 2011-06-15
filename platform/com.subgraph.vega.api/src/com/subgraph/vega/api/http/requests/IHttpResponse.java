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
package com.subgraph.vega.api.http.requests;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.html.IHTMLParseResult;

public interface IHttpResponse {
	enum ResponseStatus { RESPONSE_OK };
	ResponseStatus getResponseStatus();
	URI getRequestUri();
	int getResponseCode();
	boolean isFetchFail();
	HttpRequest getOriginalRequest();
	void setRawResponse(HttpResponse response); // temporary, probably. used in interceptor.
	HttpResponse getRawResponse();
	HttpHost getHost();
	String getBodyAsString();
	IHTMLParseResult getParsedHTML();
	boolean isMostlyAscii();
	IPageFingerprint getPageFingerprint();
	boolean lockResponseEntity();
	long getRequestMilliseconds();
	void setRequestId(long requestId);
	long getRequestId();
}
