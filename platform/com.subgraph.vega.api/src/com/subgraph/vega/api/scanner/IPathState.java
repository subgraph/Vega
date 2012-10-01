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

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;

public interface IPathState {
	IPathState getParentState();
	boolean isParametric();
	boolean doInjectionChecks();
	boolean isDone();
	void setFailed404Detection();
	boolean hasFailed404Detection();
	boolean has404Fingerprints();
	boolean has404FingerprintMatching(IPageFingerprint fp);
	boolean hasParent404FingerprintMatchingThis();
	boolean hasParent404Fingerprint(IPageFingerprint fp);
	void clear404Fingerprints();

	boolean add404Fingerprint(IPageFingerprint fp);
	void setDone();
	IWebPath getPath();
	void setResponseVaries();
	boolean getResponseVaries();
	NameValuePair getFuzzableParameter();
	IInjectionModuleContext createModuleContext();
	void setUnknownFingerprint(IPageFingerprint fp);
	IPageFingerprint getPathFingerprint();
	IPageFingerprint getUnknownFingerprint();
	void maybeAddParameters(List<NameValuePair> parameters);
	void maybeAddPostParameters(List<NameValuePair> parameters);

	int allocateXssId();
	String createXssTag(int xssId);
	String createXssTag(String prefix, int xssId);
	void registerXssRequest(HttpUriRequest request, int xssId);
	HttpUriRequest getXssRequest(int xssId, int scanId);

	HttpUriRequest createAlteredRequest(String value, boolean append);
	HttpUriRequest createRequest();

	void setResponse(IHttpResponse response);
	IHttpResponse getResponse();
	void unlockChildren();
	IPathState get404Parent();
	void setPageMissing();
	boolean matchesPathFingerprint(IPageFingerprint fp);

	void setBogusParameter();
	boolean isBogusParameter();
	void setSureDirectory();
	boolean isSureDirectory();
	boolean isRootPath();

	List<IBasicModuleScript> getInjectionModules();
	void setBadParentDirectory();
	boolean hasBadParentDirectory();

	boolean isIPSDetected();
	void setIPSDetected();
	long getScanId();

}
