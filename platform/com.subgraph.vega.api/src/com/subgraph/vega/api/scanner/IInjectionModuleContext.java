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
package com.subgraph.vega.api.scanner;

import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;

public interface IInjectionModuleContext extends IModuleContext {
	int getCurrentIndex();
	IPathState getPathState();
	int incrementResponseCount();
	boolean allResponsesReceived();
	void addRequestResponse(HttpUriRequest request, IHttpResponse response);
	void addRequestResponse(int index, HttpUriRequest request, IHttpResponse response);
	HttpUriRequest getSavedRequest(int index);
	IHttpResponse getSavedResponse(int index);
	/* Added below method because of bug #547 */
	String getSavedResponseBody(int index);
	IPageFingerprint getSavedFingerprint(int index);
	boolean isFingerprintMatch(int idx1, int idx2);
	boolean isFingerprintMatch(int idx, IPageFingerprint fp);
	void setModuleFailed();
	boolean hasModuleFailed();
	void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback, int index);
	void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback);
	void submitRequest(ICrawlerResponseProcessor callback, int flag);
	void submitAlteredRequest(ICrawlerResponseProcessor callback, String value);
	void submitAlteredRequest(ICrawlerResponseProcessor callback, String value, int flag);
	void submitAlteredRequest(ICrawlerResponseProcessor callback, String value, boolean append, int flag);
	void submitAlteredParameterNameRequest(ICrawlerResponseProcessor callback, String name, int flag);
	void submitMultipleAlteredRequests(ICrawlerResponseProcessor callback, String[] injectables);
	void submitMultipleAlteredRequests(ICrawlerResponseProcessor callback, String[] injectables, boolean append);
	
	void responseChecks(int idx);
	void responseChecks(HttpUriRequest request, IHttpResponse response);
	void contentChecks(HttpUriRequest request, IHttpResponse response);
	void pivotChecks(HttpUriRequest request, IHttpResponse response);

	void analyzePage(HttpUriRequest request, IHttpResponse response);

	List<String> getFileExtensionList();

}
