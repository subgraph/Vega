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
package com.subgraph.vega.api.crawler;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

public interface IWebCrawler {
	IHttpRequestEngine getRequestEngine();
	void submitTask(HttpUriRequest request, ICrawlerResponseProcessor callback, Object argument);
	void submitTask(HttpUriRequest request, ICrawlerResponseProcessor callback);
	void setStopOnEmptyQueue(boolean value);
	void start();
	void pause();
	void unpause();
	boolean isPaused();
	void stop() throws InterruptedException;
	void waitFinished() throws InterruptedException;
}
