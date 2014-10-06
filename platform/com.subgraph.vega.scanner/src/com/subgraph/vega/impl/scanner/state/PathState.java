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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerResult;
import com.subgraph.vega.api.analysis.MimeType;
import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.http.requests.custom.VegaHttpUriRequest;
import com.subgraph.vega.impl.scanner.requests.BasicRequestBuilder;
import com.subgraph.vega.impl.scanner.requests.GetParameterRequestBuilder;
import com.subgraph.vega.impl.scanner.requests.IRequestBuilder;
import com.subgraph.vega.impl.scanner.requests.PostParameterRequestBuilder;

public class PathState implements IPathState {

	public static PathState createBasicPathState(ICrawlerResponseProcessor fetchProcessor, PathStateManager stateManager, PathState parentState, IWebPath path) {
		final IHttpRequestEngine requestEngine = stateManager.getCrawler().getRequestEngine();
		final IRequestBuilder rb = new BasicRequestBuilder(requestEngine, path);
		final PathState st = new PathState(fetchProcessor, stateManager, parentState, path, rb);
		if(parentState != null)
			parentState.addChildState(st, true);
		else {
			st.setLocked();
			st.performInitialFetch();
		}
		return st;
	}

	public static PathState createParameterPathState(ICrawlerResponseProcessor fetchProcessor, PathState parentState, List<NameValuePair> parameters, int fuzzIndex) {
		if(parentState == null)
			throw new IllegalArgumentException("Parent of parameter path cannot be null");
		final IHttpRequestEngine requestEngine = parentState.getPathStateManager().getCrawler().getRequestEngine();
		final IRequestBuilder rb = new GetParameterRequestBuilder(requestEngine, parentState.getPath(), parameters, fuzzIndex);
		final PathState st = new PathState(fetchProcessor, parentState.getPathStateManager(), parentState, parentState.getPath(), rb);
		parentState.addChildState(st, false);
		return st;
	}

	public static PathState createPostParameterPathState(ICrawlerResponseProcessor fetchProcessor, PathState parentState, List<NameValuePair> parameters, int fuzzIndex) {
		if(parentState == null)
			throw new IllegalArgumentException("Parent of parameter path cannot be null");
		final IHttpRequestEngine requestEngine = parentState.getPathStateManager().getCrawler().getRequestEngine();
		final IRequestBuilder rb = new PostParameterRequestBuilder(requestEngine, parentState.getPath(), parameters, fuzzIndex);
		final PathState st = new PathState(fetchProcessor, parentState.getPathStateManager(), parentState, parentState.getPath(), rb);
		parentState.addChildState(st, false);
		return st;
	}

	private final PathStateManager pathStateManager;
	private final IWebPath path;
	private final PathState parentState;
	private final List<PathState> childStates = new ArrayList<PathState>();
	private final IRequestBuilder requestBuilder;
	private final PathState404 state404;
	private final ICrawlerResponseProcessor initialFetchProcessor;

	private IHttpResponse response;
	private IPageFingerprint pathFingerprint;
	private IPageFingerprint unknownFingerprint;

	private boolean isDone;
	private boolean hasFailed404;
	private boolean hasBadParent;
	private boolean ipsDetected;

	private boolean isSureDirectory;
	private boolean isPageMissing;
	private boolean isBogusParameter;
	private boolean responseVaries;
	private boolean lockedFlag;
	private PathStateParameterManager parameterManager;
	
	private final Object childCountLock = new Object();
	private int descendantCount = 0;
	private int childCount = 0;
	
	private AtomicInteger outstandingRequests = new AtomicInteger();
	private AtomicInteger fuzzCounter = new AtomicInteger(0);
	
	private volatile boolean finishOnNoRequests;
	
	private PathState(ICrawlerResponseProcessor fetchProcessor, PathStateManager stateManager, PathState parentState, IWebPath path, IRequestBuilder requestBuilder) {
		this.initialFetchProcessor = fetchProcessor;
		this.pathStateManager = stateManager;
		this.path = path;
		this.parentState = parentState;
		this.requestBuilder = requestBuilder;
		this.state404 = new PathState404(this);
	}

	@Override
	public PathState getParentState() {
		return parentState;
	}

	private void addChildState(PathState state, boolean checkDup) {
		synchronized(childStates) {
			if(checkDup) {
				for(IPathState cs: childStates) {
					if(cs.getPath().equals(state.getPath()))
						return;
				}
			}
			childStates.add(state);
			if(lockedFlag)
				state.setLocked();
			else
				state.performInitialFetch();
			
			synchronized(childCountLock) {
				childCount += 1;
				incrementDescendants();
			}
		}
	}

	private void incrementDescendants() {
		synchronized(childCountLock) {
			descendantCount += 1;
			if(parentState != null) {
				parentState.incrementDescendants();
			}
		}
	}

	public int getDescendantCount() {
		return descendantCount;
	}
	
	public int getChildCount() {
		return childCount;
	}

	public synchronized int getDepth() {
		if(parentState == null) {
			return 1;
		} else {
			return 1 + parentState.getDepth();
		}
	}

	public PathStateManager getPathStateManager() {
		return pathStateManager;
	}
	@Override
	public boolean isParametric() {
		return requestBuilder.isFuzzable();
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public synchronized void setDone() {
		if (!isDone) {
			isDone = true;
			response = null;
			pathStateManager.notifyPathNodeFinish(this);
		}
	}

	public void requeueInitialFetch() {
		if(lockedFlag) {
			return;
		}
		final IInjectionModuleContext ctx = new ModuleContext(pathStateManager, requestBuilder, this, 0);
		final HttpUriRequest req = createRequest();
		submitRequest(req, initialFetchProcessor, ctx);
	}

	private void setLocked() {
		lockedFlag = true;
	}

	private void performInitialFetch() {
		final IInjectionModuleContext ctx = new ModuleContext(pathStateManager, requestBuilder, this, 0);
		final HttpUriRequest req = createRequest();

		if(response != null) {
			initialFetchProcessor.processResponse(pathStateManager.getCrawler(), req, response, ctx);
		} else {
			submitRequest(req, initialFetchProcessor, ctx);
		}
		pathStateManager.notifyPathNodeStart(this);
	}

	@Override
	public void setBogusParameter() {
		isBogusParameter = true;
	}

	@Override
	public boolean isBogusParameter() {
		return isBogusParameter;
	}

	@Override
	public IWebPath getPath() {
		return path;
	}

	@Override
	public void setUnknownFingerprint(IPageFingerprint fp) {
		unknownFingerprint = fp;
	}

	@Override
	public IPageFingerprint getUnknownFingerprint() {
		return unknownFingerprint;
	}

	public void submitRequest(ICrawlerResponseProcessor callback) {
		final HttpUriRequest req = requestBuilder.createBasicRequest();
		final IInjectionModuleContext ctx = createModuleContext();
		submitRequest(req, callback, ctx);
	}

	public void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback) {
		final IInjectionModuleContext ctx = createModuleContext();
		submitRequest(request, callback, ctx);
	}

	public void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback, IInjectionModuleContext ctx) {
		incrementOutstandingRequests();
		pathStateManager.getCrawler().submitTask(request, getWrappedCallback(callback), ctx);
	}

	private ICrawlerResponseProcessor getWrappedCallback(ICrawlerResponseProcessor callback) {
		if(pathStateManager.requestLoggingEnabled())
			return CrawlerCallbackWrapper.createLogging(this, pathStateManager.getRequestLog(), callback);
		else
			return CrawlerCallbackWrapper.create(this, callback);
	}

	@Override
	public HttpUriRequest createRequest() {
		return requestBuilder.createBasicRequest();
	}

	@Override
	public HttpUriRequest createAlteredRequest(String value, boolean append) {
		return requestBuilder.createAlteredRequest(value, append);
	}

	public boolean hasMaximum404Fingerprints() {
		return state404.hasMaximum404Fingerprints();
	}

	@Override
	public boolean add404Fingerprint(IPageFingerprint fp) {
		return state404.add404Fingerprint(fp);
	}

	@Override
	public boolean isRootPath() {
		return path.getParentPath() == null;
	}

	@Override
	public boolean has404Fingerprints() {
		return state404.has404Fingerprints();
	}

	@Override
	public IPathState get404Parent() {
		return state404.get404Parent();
	}

	@Override
	public boolean has404FingerprintMatching(IPageFingerprint fp) {
		return state404.has404FingerprintMatching(fp);
	}
	@Override
	public boolean hasParent404Fingerprint(IPageFingerprint fp) {
		return state404.hasParent404Fingerprint(fp);
	}

	public void dump404Fingerprints() {
		state404.dumpFingerprints();
	}
	@Override
	public boolean hasParent404FingerprintMatchingThis() {
		return state404.hasParent404Fingerprint(pathFingerprint);
	}

	@Override
	public void setSureDirectory() {
		isSureDirectory = true;
	}

	@Override
	public void clear404Fingerprints() {
		state404.clear404Fingerprints();
	}

	public void setSkip404() {
		state404.setSkip404();
	}

	public boolean getSkip404() {
		return state404.getSkip404();
	}

	@Override
	public boolean isSureDirectory() {
		return isSureDirectory;
	}

	@Override
	public void setResponse(IHttpResponse response) {
		this.response = response;
		if(response != null) {
			this.pathFingerprint = response.getPageFingerprint();
		} else {
			this.pathFingerprint = null;
			return;
		}

		if(response.getResponseCode() == 200) {
			addWebResponseToPath(response);
		}
	}

	private void addWebResponseToPath(IHttpResponse response) {
		final IContentAnalyzer contentAnalyzer = pathStateManager.getContentAnalyzer();
		final IContentAnalyzerResult result = contentAnalyzer.processResponse(response, false, false);
		final URI uri = createRequest().getURI();
		final String mimeType = contentAnalyzerResultToMimeString(result);
		if(uri.getQuery() == null) {
			if(path.getMimeType() == null && mimeType != null) {
				path.setMimeType(mimeType);
			}
			return;
		}
		path.addGetResponse(uri.getQuery(), mimeType);
	}

	private String contentAnalyzerResultToMimeString(IContentAnalyzerResult result) {
		if(result.getSniffedMimeType() != MimeType.MIME_NONE)
			return result.getSniffedMimeType().getCanonicalName();
		else if(result.getDeclaredMimeType() != MimeType.MIME_NONE)
			return result.getDeclaredMimeType().getCanonicalName();
		else
			return null;
	}

	@Override
	public IHttpResponse getResponse() {
		return response;
	}

	public boolean isPageMissing() {
		return isPageMissing;
	}

	@Override
	public void setPageMissing() {
		isPageMissing = true;
	}

	@Override
	public void setResponseVaries() {
		responseVaries = true;
	}

	@Override
	public boolean getResponseVaries() {
		return responseVaries;
	}

	public void debug(String msg) {
		pathStateManager.debug("["+path.getUri()+"] "+ msg);
	}

	@Override
	public IPageFingerprint getPathFingerprint() {
		return pathFingerprint;
	}

	@Override
	public boolean matchesPathFingerprint(IPageFingerprint fp) {
		if(pathFingerprint == null) {
			debug("Whoops no path fingerprint for "+ path.getUri() + " : " + this);
			return false;
		}
		return pathFingerprint.isSame(fp);
	}

	@Override
	public long getScanId() {
		return pathStateManager.getScanId();
	}
	@Override
	public int allocateXssId() {
		return pathStateManager.allocateXssId();
	}
	@Override
	public String createXssTag(int xssId) {
		return pathStateManager.createXssTag(xssId);
	}
	@Override
	public String createXssTag(String prefix, int xssId) {
		return pathStateManager.createXssTag(prefix, xssId);
	}
	@Override
	public String createXssPattern(int xssId) {
		return pathStateManager.createXssPattern(xssId);
	}
	@Override
	public String createXssPattern(String prefix, int xssId) {
		return pathStateManager.createXssPattern(prefix, xssId);
	}
	@Override
	public void registerXssRequest(HttpUriRequest request, int xssId) {
		pathStateManager.registerXssRequest(request, xssId);
	}

	@Override
	public HttpUriRequest getXssRequest(int xssId, int scanId) {
		return pathStateManager.getXssRequest(xssId, scanId);
	}

	@Override
	public NameValuePair getFuzzableParameter() {
		return requestBuilder.getFuzzableParameter();
	}

	@Override
	public void maybeAddParameters(List<NameValuePair> parameters) {
		final PathStateParameterManager pm = getParameterManager();
		synchronized(pm) {
			if(parameters.size() > pathStateManager.getMaxParameterCount()) {
				return;
			} else if(pm.hasParameterList(parameters)) {
				if(isRescanNeeded()) {
					rescanGetParameters(parameters, pm);
				} 
			} else if(!pathStateManager.hasExceededLimits(this)) {
				pm.addParameterList(parameters);
			}
		}
	}

	@Override
	public void maybeAddPostParameters(List<NameValuePair> parameters) {
		final PathStateParameterManager pm = getParameterManager();
		synchronized(pm) {
			if(parameters.size() > pathStateManager.getMaxParameterCount()) {
				return;
			} else if(!pm.hasPostParameterList(parameters)) {
				pm.addPostParameterList(parameters);
			} else if(isRescanNeeded()) {
				rescanPostParameters(parameters,  pm);
			}
		}
	}
	
	private boolean isRescanNeeded() {
		if(!pathStateManager.isProxyScan() || response == null) {
			return false;
		}
		
		final IHttpRequestEngine requestEngine = pathStateManager.getCrawler().getRequestEngine();
		final List<Cookie> cookies = requestEngine.getCookiesForRequest(response.getHost(), response.getOriginalRequest());
		return !sameCookies(cookies, response.getSentCookies());
	}
	
	private boolean sameCookies(List<Cookie> a, List<Cookie> b) {
		if(a.size() != b.size()) {
			return false;
		}
		for(Cookie c: a) {
			Cookie c2 = findFirstCookieByName(b, c.getName());
			if(c2 == null) {
				return false;
			}
		}
		return true;
	}
	
	private Cookie findFirstCookieByName(List<Cookie> cookieList, String name) {
		for(Cookie c: cookieList) {
			if(c.getName().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}
	
	private void rescanGetParameters(List<NameValuePair> parameters, PathStateParameterManager manager) {
		rescanPathStates(manager.getStatesForParameterList(parameters));
	}
	
	private void rescanPostParameters(List<NameValuePair> parameters, PathStateParameterManager manager) {
		rescanPathStates(manager.getStatesForPostParameterList(parameters));
	}
	
	private void rescanPathStates(List<PathState> states) {
		for(PathState ps: states) {
			ps.requeueInitialFetch();
		}
	}

	private synchronized PathStateParameterManager getParameterManager() {
		if(parameterManager == null) {
			parameterManager = new PathStateParameterManager(this);
		}
		return parameterManager;
	}

	@Override
	public void unlockChildren() {
		synchronized(childStates) {
			if(!lockedFlag)
				return;
			lockedFlag = false;
			for(PathState c: childStates) {
				c.performInitialFetch();
			}
		}
	}

	@Override
	public String toString() {
		return "STATE: ["+ requestBuilder + "]";
	}

	@Override
	public IInjectionModuleContext createModuleContext() {
		return new ModuleContext(pathStateManager, requestBuilder, this);
	}

	@Override
	public void setFailed404Detection() {
		hasFailed404 = true;
	}

	@Override
	public boolean hasFailed404Detection() {
		return hasFailed404;
	}

	@Override
	public void setBadParentDirectory() {
		hasBadParent = true;
	}

	@Override
	public boolean hasBadParentDirectory() {
		return hasBadParent;
	}

	@Override
	public boolean isIPSDetected() {
		return ipsDetected;
	}

	@Override
	public void setIPSDetected() {
		ipsDetected = true;
	}

	@Override
	public boolean doInjectionChecks() {
		if(isParametric())
			return true;
		else if(path.getPathType() == PathType.PATH_DIRECTORY)
			return pathStateManager.getDirectoryInjectionChecksFlag();
		else
			return pathStateManager.getNonParameterFileInjectionChecksFlag();
	}

	@Override
	public List<IBasicModuleScript> getInjectionModules() {
		return pathStateManager.getInjectionModules();
	}

	@Override
	public IHttpRequestEngine getRequestEngine() {
		return pathStateManager.getCrawler().getRequestEngine();
	}
	
	public HttpUriRequest createRawRequest(HttpHost httpHost, String method, String uriString) {
		VegaHttpUriRequest request = new VegaHttpUriRequest(httpHost, method, uriString);
		return getRequestEngine().createRawRequest(httpHost, request.getRequestLine());
	
	}
	
	void incrementOutstandingRequests() {
		outstandingRequests.incrementAndGet();
	}
	
	public void decrementOutstandingRequests() {
		if((outstandingRequests.decrementAndGet() <= 0) && (fuzzCounter.get() <= 0)) {
			if(finishOnNoRequests) {
				setDone();
			}
		}
	}

	public void incrementFuzzCounter() {
		fuzzCounter.incrementAndGet();
	}
	
	public void decrementFuzzCounter() {
		if((fuzzCounter.decrementAndGet() <= 0) && (outstandingRequests.get() <= 0)) {
			if(finishOnNoRequests) {
				setDone();
			}
		}
	}
	
	public void setFinishOnNoRequests() {
		finishOnNoRequests = true;
		if(outstandingRequests.get() == 0) {
			setDone();
		}
	}
}
