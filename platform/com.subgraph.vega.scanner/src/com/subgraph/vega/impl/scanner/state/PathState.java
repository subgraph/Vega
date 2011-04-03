package com.subgraph.vega.impl.scanner.state;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerResult;
import com.subgraph.vega.api.analysis.MimeType;
import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

public class PathState implements IPathState {
	
	public static PathState createBasicPathState(ICrawlerResponseProcessor fetchProcessor, PathStateManager stateManager, PathState parentState, IWebPath path) {
		final PathState st = new PathState(fetchProcessor, stateManager, parentState, path, null, 0);
		if(parentState != null)
			parentState.addChildState(st);
		else
			st.performInitialFetch();
		return st;
	}
	
	public static PathState createParameterPathState(ICrawlerResponseProcessor fetchProcessor, PathState parentState, List<NameValuePair> parameters, int fuzzIndex) {
		if(parentState == null)
			throw new IllegalArgumentException("Parent of parameter path cannot be null");
		final PathState st = new PathState(fetchProcessor, parentState.getPathStateManager(), parentState, parentState.getPath(), parameters, fuzzIndex);
		parentState.addChildState(st);
		return st;
	}
	
	private final PathStateManager pathStateManager;
	private final IWebPath path;
	private final PathState parentState;
	private final List<PathState> childStates = new ArrayList<PathState>();
	private final PathStateRequestBuilder requestBuilder;
	private final PathState404 state404;
	private final boolean isParametric;
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
	
	private PathState(ICrawlerResponseProcessor fetchProcessor, PathStateManager stateManager, PathState parentState, IWebPath path, List<NameValuePair> parameters, int index) {
		this.initialFetchProcessor = fetchProcessor;
		this.pathStateManager = stateManager;
		this.path = path;
		this.parentState = parentState;
		this.isParametric = (parameters != null);
		this.requestBuilder = (isParametric) ? (new PathStateRequestBuilder(path, parameters, index)) : (new PathStateRequestBuilder(path));
		this.state404 = new PathState404(this);
	}
	
	public PathState getParentState() {
		return parentState;
	}
	
	public IScannerModuleRegistry getModuleRegistry() {
		return pathStateManager.getModuleRegistry();
	}

	public void addChildState(PathState state) {
		synchronized(childStates) {
			for(IPathState cs: childStates) {
				if(cs.getPath().equals(state))
					return;
			}
			childStates.add(state);
			if(lockedFlag)
				state.setLocked();
			else
				state.performInitialFetch();
		}
	}
	
	public PathStateManager getPathStateManager() {
		return pathStateManager;
	}
	public boolean isParametric() {
		return isParametric;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public void setDone() {
		isDone = true;
	}
	
	private void setLocked() {
		lockedFlag = true;
	}
	
	private void performInitialFetch() {
		final IModuleContext ctx = new ModuleContext(pathStateManager, requestBuilder, this, 0);
		final HttpUriRequest req = createRequest();

		if(response != null) {
			initialFetchProcessor.processResponse(pathStateManager.getCrawler(), req, response, ctx);
		} else {
			submitRequest(req, initialFetchProcessor, ctx);
		}
	}
	
	public void setBogusParameter() {
		isBogusParameter = true;
	}
	
	public boolean isBogusParameter() {
		return isBogusParameter;
	}
	
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
		final HttpUriRequest req = requestBuilder.createGetRequest();
		final IModuleContext ctx = createModuleContext();
		submitRequest(req, callback, ctx);
	}

	public void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback) {
		final IModuleContext ctx = createModuleContext();
		submitRequest(request, callback, ctx);
	}
	
	public void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback, IModuleContext ctx) {
		pathStateManager.getCrawler().submitTask(request, callback, ctx);
	}
	
	
	public HttpUriRequest createRequest() {
		return requestBuilder.createGetRequest();
	}
	
	public HttpUriRequest createAlteredRequest(String value, boolean append) {
		return requestBuilder.createAlteredRequest(value, append);
	}
	
	public boolean hasMaximum404Fingerprints() {
		return state404.hasMaximum404Fingerprints();
	}
	
	public boolean add404Fingerprint(IPageFingerprint fp) {
		return state404.add404Fingerprint(fp);
	}
	
	public boolean isRootPath() {
		return path.getParentPath() == null;
	}
	
	public boolean has404Fingerprints() {
		return state404.has404Fingerprints();
	}
	
	public IPathState get404Parent() {
		return state404.get404Parent();
	}
	
	public boolean has404FingerprintMatching(IPageFingerprint fp) {
		return state404.has404FingerprintMatching(fp);
	}
	public boolean hasParent404Fingerprint(IPageFingerprint fp) {
		return state404.hasParent404Fingerprint(fp);
	}
	
	public void dump404Fingerprints() {
		state404.dumpFingerprints();
	}
	public boolean hasParent404FingerprintMatchingThis() {
		return state404.hasParent404Fingerprint(pathFingerprint);
	}
	
	public void setSureDirectory() {
		isSureDirectory = true;
	}
	
	public void clear404Fingerprints() {
		state404.clear404Fingerprints();
	}
	
	public void setSkip404() {
		state404.setSkip404();
	}
	
	public boolean getSkip404() {
		return state404.getSkip404();
	}
	
	public boolean isSureDirectory() {
		return isSureDirectory;
	}
	
	public void setResponse(IHttpResponse response) {
		this.response = response;
		if(response != null) {
			this.pathFingerprint = response.getPageFingerprint();
		}
		else
			this.pathFingerprint = null;
		
		if(response.getResponseCode() == 200) {
			addWebResponseToPath(response);
		}
	}
	
	private void addWebResponseToPath(IHttpResponse response) {
		final IContentAnalyzer contentAnalyzer = pathStateManager.getContentAnalyzer();
		final IContentAnalyzerResult result = contentAnalyzer.processResponse(response, false, false);
		final URI uri = createRequest().getURI();
		path.addGetResponse(uri.getQuery(), contentAnalyzerResultToMimeString(result));
	}
	
	private String contentAnalyzerResultToMimeString(IContentAnalyzerResult result) {
		if(result.getSniffedMimeType() != MimeType.MIME_NONE)
			return result.getSniffedMimeType().getCanonicalName();
		else if(result.getDeclaredMimeType() != MimeType.MIME_NONE)
			return result.getDeclaredMimeType().getCanonicalName();
		else 
			return null;
	}
	
	public IHttpResponse getResponse() {
		return response;
	}
	
	public boolean isPageMissing() {
		return isPageMissing;
	}
	
	public void setPageMissing() {
		isPageMissing = true;
	}
	
	public void setResponseVaries() {
		responseVaries = true;
	}
	
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
	
	public boolean matchesPathFingerprint(IPageFingerprint fp) {
		if(pathFingerprint == null) {
			debug("Whoops no path fingerprint for "+ path.getUri() + " : " + this);
			return false;
		}
		return pathFingerprint.isSame(fp);
	}
	
	public int allocateXssId() {
		return pathStateManager.allocateXssId();
	}
	public String createXssTag(int xssId) {
		return pathStateManager.createXssTag(xssId);
	}
	
	public String createXssTag(String prefix, int xssId) {
		return pathStateManager.createXssTag(prefix, xssId);
	}
	
	public void registerXssRequest(HttpUriRequest request, int xssId) {
		pathStateManager.registerXssRequest(request, xssId);
	}
	
	public HttpUriRequest getXssRequest(int xssId, int scanId) {
		return pathStateManager.getXssRequest(xssId, scanId);
	}
	
	public NameValuePair getFuzzableParameter() {
		return requestBuilder.getFuzzableParameter();
	}
	
	public void maybeAddParameters(List<NameValuePair> parameters) {
		final PathStateParameterManager pm = getParameterManager();
		synchronized(pm) {
			if(!pm.hasParameterList(parameters))
				pm.addParameterList(parameters);
		}		
	}
	private synchronized PathStateParameterManager getParameterManager() {
		if(parameterManager == null) {
			parameterManager = new PathStateParameterManager(this);
		}
		return parameterManager;
	}
	
	public void unlockChildren() {
		if(!lockedFlag)
			return;
		lockedFlag = false;
		synchronized(childStates) {
			for(PathState c: childStates) {
				c.performInitialFetch();
			}
		}
	}
	
	public String toString() {
		return "STATE: ["+ path.toString() + "]";
	}
	
	@Override
	public IModuleContext createModuleContext() {
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
}
