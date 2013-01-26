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
package com.subgraph.vega.impl.scanner.state;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.requests.IRequestLog;

public class CrawlerCallbackWrapper implements ICrawlerResponseProcessor {
	static ICrawlerResponseProcessor createLogging(PathState pathState, IRequestLog requestLog, ICrawlerResponseProcessor callback) {
		return new CrawlerCallbackWrapper(pathState, requestLog, callback);
	}
	
	static ICrawlerResponseProcessor create(PathState pathState, ICrawlerResponseProcessor callback) {
		return new CrawlerCallbackWrapper(pathState, null, callback);
	}
	
	private final PathState pathState;
	private final boolean logRequest;
	private final IRequestLog requestLog;
	private final ICrawlerResponseProcessor wrappedCallback;
	
	private CrawlerCallbackWrapper(PathState pathState, IRequestLog requestLog, ICrawlerResponseProcessor callback) {
		this.pathState = pathState;
		this.logRequest = (requestLog != null);
		this.requestLog = requestLog;
		this.wrappedCallback = callback;
		
	}

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, Object argument) {
		pathState.decrementOutstandingRequests();
		
		if(!response.lockResponseEntity()) {
			return;
		}
		wrappedCallback.processResponse(crawler, request, response, argument);
		if(logRequest) {
			requestLog.addRequestResponse(response);
		}
	}

	@Override
	public void processException(HttpUriRequest request, Throwable ex, Object argument) {
		pathState.decrementOutstandingRequests();
		pathState.getPathStateManager().reportRequestException(request, ex);
	}
}
