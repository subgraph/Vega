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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.impl.scanner.requests.IRequestBuilder;

public class ModuleContext implements IInjectionModuleContext {
	private final static Logger logger = Logger.getLogger("scanner");
	private final PathStateManager scanState;
	private final IRequestBuilder requestBuilder;
	private final IPathState pathState;
	private final int currentIndex;
	private final ModuleContextState contextState;
	private final List<String> stringHighlights;
	private final List<String> regexHighlights;
	private final List<String> caseInsensitiveRegexHighlights;


	ModuleContext(PathStateManager scanState, IRequestBuilder requestBuilder, IPathState pathState, int index) {
		this.scanState = scanState;
		this.requestBuilder = requestBuilder;
		this.pathState = pathState;
		currentIndex = index;
		contextState = new ModuleContextState();
		this.stringHighlights = new ArrayList<String>();
		this.regexHighlights = new ArrayList<String>();
		this.caseInsensitiveRegexHighlights = new ArrayList<String>();

	}

	ModuleContext(PathStateManager scanState, IRequestBuilder requestBuilder, IPathState pathState) {
		this(scanState, requestBuilder, pathState, -1);
	}

	private ModuleContext(ModuleContext ctx, int index) {
		scanState = ctx.scanState;
		requestBuilder = ctx.requestBuilder;
		pathState = ctx.pathState;
		contextState = ctx.contextState;
		currentIndex = index;
		this.stringHighlights = new ArrayList<String>(ctx.stringHighlights);
		this.regexHighlights = new ArrayList<String>(ctx.regexHighlights);
		this.caseInsensitiveRegexHighlights = new ArrayList<String>();
	}

	@Override
	public IPathState getPathState() {
		return pathState;
	}

	@Override
	public int incrementResponseCount() {
		return contextState.incrementResponseCount();
	}
	

	@Override
	public boolean allResponsesReceived() {
		return contextState.allResponsesReceieved();
	}

	@Override
	public void addRequestResponse(HttpUriRequest request,
			IHttpResponse response) {
		if (currentIndex == -1)
			throw new IllegalStateException(
					"Cannot add request/response because index has not been specified");
		contextState.addRequestResponse(currentIndex, request, response);
	}

	@Override
	public void addRequestResponse(int index, HttpUriRequest request,
			IHttpResponse response) {
		contextState.addRequestResponse(index, request, response);
	}

	@Override
	public HttpUriRequest getSavedRequest(int index) {
		return contextState.getSavedRequest(index);
	}

	@Override
	public IHttpResponse getSavedResponse(int index) {
		return contextState.getSavedResponse(index);
	}

	/* Added below method because of bug #547 */
	
	@Override
	public String getSavedResponseBody(int index) {
		return contextState.getSavedResponseBody(index);

	}
	@Override
	public IPageFingerprint getSavedFingerprint(int index) {
		return contextState.getSavedFingerprint(index);
	}

	@Override
	public boolean isFingerprintMatch(int idx1, int idx2) {
		final IPageFingerprint fp1 = getSavedFingerprint(idx1);
		final IPageFingerprint fp2 = getSavedFingerprint(idx2);
		if (fp1 == null || fp2 == null)
			return false;
		return fp1.isSame(fp2);
	}

	@Override
	public boolean isFingerprintMatch(int idx, IPageFingerprint fp) {
		final IPageFingerprint fp2 = getSavedFingerprint(idx);
		if (fp == null || fp2 == null)
			return false;
		return fp.isSame(fp2);
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void submitRequest(HttpUriRequest request,
			ICrawlerResponseProcessor callback, int index) {
		contextState.incrementSentRequestCount();
		scanState.getCrawler().submitTask(request, getWrappedCallback(callback), new ModuleContext(this, index));
	}
	
	private ICrawlerResponseProcessor getWrappedCallback(ICrawlerResponseProcessor callback) {
		if(scanState.requestLoggingEnabled())
			return CrawlerCallbackWrapper.createLogging(scanState.getRequestLog(), callback);
		else
			return CrawlerCallbackWrapper.create(callback);
	}
	
	@Override
	public void submitRequest(HttpUriRequest request,
			ICrawlerResponseProcessor callback) {
		submitRequest(request, callback, 0);
	}

	@Override
	public void submitRequest(ICrawlerResponseProcessor callback, int flag) {
		final HttpUriRequest req = requestBuilder.createBasicRequest();
		if (req != null)
			submitRequest(req, callback, flag);
	}

	@Override
	public void submitAlteredRequest(ICrawlerResponseProcessor callback,
			String value) {
		submitAlteredRequest(callback, value, false, 0);
	}

	@Override
	public void submitAlteredRequest(ICrawlerResponseProcessor callback,
			String value, int flag) {
		submitAlteredRequest(callback, value, false, flag);
	}

	@Override
	public void submitAlteredRequest(ICrawlerResponseProcessor callback,
			String value, boolean append, int flag) {
		final HttpUriRequest req = requestBuilder.createAlteredRequest(value,
				append);
		if (req != null)
			submitRequest(req, callback, flag);
	}

	@Override
	public void submitAlteredParameterNameRequest(
			ICrawlerResponseProcessor callback, String name, int flag) {
		final HttpUriRequest req = requestBuilder
				.createAlteredParameterNameRequest(name);
		if (req != null)
			submitRequest(req, callback, flag);
	}

	@Override
	public void submitMultipleAlteredRequests(
			ICrawlerResponseProcessor callback, String[] injectables) {
		submitMultipleAlteredRequests(callback, injectables, false);
	}

	@Override
	public void submitMultipleAlteredRequests(
			ICrawlerResponseProcessor callback, String[] injectables,
			boolean append) {
		for (int i = 0; i < injectables.length; i++)
			submitAlteredRequest(callback, injectables[i], append, i);
	}

	@Override
	public void setModuleFailed() {
		contextState.setModuleFailed();
	}

	@Override
	public boolean hasModuleFailed() {
		return contextState.hasModuleFailed();
	}

	@Override
	public void error(HttpUriRequest request, IHttpResponse response,
			String message) {
		final long requestId = scanState.getRequestLog().addRequestResponse(response);
		logger.warning("Error running module: "+ message + " (request logged with id="+ requestId +")");
	}

	@Override
	public void debug(String msg) {
		scanState.debug("[" + pathState.getPath().getUri() + "] " + msg);
	}

	@Override
	public void analyzePage(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzePage(this, request, response);
	}

	@Override
	public void responseChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzeContent(this, request, response);
		scanState.analyzePage(this, request, response);
	}

	@Override
	public void contentChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzeContent(this, request, response);
	}

	@Override
	public void responseChecks(int idx) {
		final HttpUriRequest req = getSavedRequest(idx);
		final IHttpResponse res = getSavedResponse(idx);
		if (req != null && res != null)
			responseChecks(req, res);
	}

	@Override
	public void pivotChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzePivot(this, request, response);
		scanState.analyzeContent(this, request, response);
		scanState.analyzePage(this, request, response);
	}
	
	
	public void publishAlert(String type, String message, HttpRequest request, IHttpResponse response, Object ...properties) {
		publishAlert(type, null, message, request, response, properties);
	}

	public void publishAlert(String type, String key, String message, HttpRequest request, IHttpResponse response, Object ...properties) {
		debug("Publishing Alert: ("+ type + ") ["+ request.getRequestLine().getUri() + "] ");
		final IScanInstance scan = scanState.getScanInstance();
		final IRequestLog requestLog = scanState.getRequestLog();
		synchronized(scan) {
			if(key != null && scan.hasAlertKey(key))
				return;
			final long requestId = requestLog.addRequestResponse(response);
			final IScanAlert alert = scan.createAlert(type, key, requestId);
			for(int i = 0; (i + 1) < properties.length; i += 2) {
				if(properties[i] instanceof String) {
					alert.setProperty((String) properties[i], properties[i + 1]);
				} else {
					logger.warning("Property key passed to publishAlert() is not a string");
				}
			}
			if (pathState.isParametric()) {
				if (pathState.getFuzzableParameter() != null) {
					alert.setProperty("param", pathState.getFuzzableParameter().getName());
				}
			}
			
			/*
			alert.setProperty("methods", request.getMethod());
			*/
			
			if(message != null)
				alert.setStringProperty("message", message);
			
			for(String hl: stringHighlights) {
				alert.addStringMatchHighlight(hl);
			}
			for(String hl: regexHighlights) {
				alert.addRegexHighlight(hl);
			}
			for (String hl: caseInsensitiveRegexHighlights) {
				alert.addRegexCaseInsensitiveHighlight(hl);
			}

			scan.addAlert(alert);
		}
	}
	
	public List<String> getFileExtensionList() {
		return scanState.getFileExtensionList();
	}

	@Override
	public void setProperty(String name, Object value) {
		scanState.getScanInstance().setProperty(name, value);
	}

	@Override
	public void setStringProperty(String name, String value) {
		scanState.getScanInstance().setStringProperty(name, value);		
	}

	@Override
	public void setIntegerProperty(String name, int value) {
		scanState.getScanInstance().setIntegerProperty(name, value);		
	}

	@Override
	public Object getProperty(String name) {
		return scanState.getScanInstance().getProperty(name);
	}

	@Override
	public String getStringProperty(String name) {
		return scanState.getScanInstance().getStringProperty(name);
	}

	@Override
	public Integer getIntegerProperty(String name) {
		return scanState.getScanInstance().getIntegerProperty(name);
	}

	@Override
	public List<String> propertyKeys() {
		return scanState.getScanInstance().propertyKeys();
	}
	
	@Override
	public void addStringHighlight(String str) {
		stringHighlights.add(str);
	}

	@Override
	public void addRegexHighlight(String regex) {
		try {
			Pattern.compile(regex);
			regexHighlights.add(regex);
		} catch (PatternSyntaxException e) {
			logger.warning("Invalid regular expression '"+ regex +"' passed to addHighlightRegex(): "+ e.getDescription());
		}
	}
	
	@Override
	public void addRegexCaseInsensitiveHighlight(String regex) {		
		try {
			Pattern.compile(regex);
			caseInsensitiveRegexHighlights.add(regex);
		} catch (PatternSyntaxException e) {
			logger.warning("Invalid regular expression '"+ regex +"' passed to addHighlightRegex(): "+ e.getDescription());
		}
	}
	
	@Override
	public void reset() {
		stringHighlights.clear();
		regexHighlights.clear();
	}
}
