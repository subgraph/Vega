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
package com.subgraph.vega.internal.model.requests;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;

import com.db4o.ObjectContainer;

public class HttpMessageCloner {

	private final ObjectContainer database;

	HttpMessageCloner(ObjectContainer database) {
		this.database = database;
	}

	HttpRequest copyRequest(HttpRequest request) {
		if(request instanceof HttpEntityEnclosingRequest) {
			return copyEntityEnclosingRequest((HttpEntityEnclosingRequest) request);
		} else {
			return copyBasicRequest(request);
		}
	}

	HttpResponse copyResponse(HttpResponse response) {
		final HttpEntity e = copyEntity(response.getEntity());
		final RequestLogResponse r = new RequestLogResponse(database, response.getStatusLine(), entityToDatabaseId(e));
		copyHeaders(response, r);
		return r;
	}

	private HttpRequest copyEntityEnclosingRequest(HttpEntityEnclosingRequest request) {
		final HttpEntity e = copyEntity(request.getEntity());
		final RequestLogEntityEnclosingRequest r = new RequestLogEntityEnclosingRequest(database, request.getRequestLine(), entityToDatabaseId(e));
		copyHeaders(request, r);
		return r;
	}

	private HttpRequest copyBasicRequest(HttpRequest request) {
		if(request == null) {
			return null;
		}
		final HttpRequest r = new BasicHttpRequest(request.getRequestLine());
		copyHeaders(request, r);
		return r;
	}

	private static void copyHeaders(HttpMessage from, HttpMessage to) {
		for(Header h: from.getAllHeaders()) {
			to.addHeader(new BasicHeader(h.getName(), h.getValue()));
		}
	}

	private long entityToDatabaseId(HttpEntity entity) {
		if(entity == null) {
			return 0;
		}
		database.ext().store(entity);
		return database.ext().getID(entity);
	}

	private HttpEntity copyEntity(HttpEntity entity) {
		try {
			if(entity == null) {
				return null;
			}
			final byte[] content = EntityUtils.toByteArray(entity);
			return new RequestLogEntity(content, entity.getContentType(), entity.getContentEncoding());
		} catch (IOException e) {
			return null;
		}
	}
}
