package com.subgraph.vega.impl.scanner.state;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.handlers.DirectoryProcessor;
import com.subgraph.vega.impl.scanner.handlers.FileProcessor;
import com.subgraph.vega.impl.scanner.handlers.UnknownProcessor;

public class PathState implements IPathState {
		
	private final static ICrawlerResponseProcessor fetchFileProcessor = new FileProcessor();
	private final static ICrawlerResponseProcessor fetchDirProcessor = new DirectoryProcessor();
	private final static ICrawlerResponseProcessor fetchUnknownProcessor = new UnknownProcessor();
	
	private final PathStateManager scanState;
	private final IWebPath path;
	private final PathState parentState;
	private final List<PathState> childStates = new ArrayList<PathState>();
	private final PathStateRequestBuilder requestBuilder;
	private final PathState404 state404;
	private final boolean isParametric;
	private IHttpResponse response;
	private IPageFingerprint pathFingerprint;
	private IPageFingerprint unknownFingerprint;
	
	private boolean isDone;
	
	private boolean isSureDirectory;
	private boolean isPageMissing;
	private boolean isBogusParameter;
	private boolean responseVaries;
	private boolean lockedFlag;
	private boolean initialChecksFinished;
	
	public PathState(PathStateManager state, PathState parentState, IWebPath path, List<NameValuePair> parameters, int index) {
		this.scanState = state;
		this.path = path;
		this.parentState = parentState;
		this.requestBuilder = new PathStateRequestBuilder(path, parameters, index);
		this.state404 = new PathState404(this);
		this.isParametric = true;
		this.initialChecksFinished = false;
		
	}
	public PathState(PathStateManager state, PathState parentState, IWebPath path) {
		this.scanState = state;
		this.path = path;
		this.parentState = parentState;
		this.requestBuilder = new PathStateRequestBuilder(path);
		this.state404 = new PathState404(this);
		this.isParametric = false;
		this.initialChecksFinished = false;
	}
	
	public PathState getParentState() {
		return parentState;
	}
	
	public IScannerModuleRegistry getModuleRegistry() {
		return scanState.getModuleRegistry();
	}

	public void addChildState(PathState state) {
		synchronized(childStates) {
			for(IPathState cs: childStates) {
				if(cs.getPath().equals(state))
					return;
			}
			childStates.add(state);
		}
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
	
	public boolean getInitialChecksFinished() {
		return initialChecksFinished;
	}
	
	public void setInitialChecksFinished() {
		initialChecksFinished = true;
	}
	
	public void setLocked() {
		lockedFlag = true;
	}
	
	public void setUnlocked() {
		lockedFlag = false;
	}
	
	public boolean isLocked() {
		return  lockedFlag;
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
		scanState.getCrawler().submitTask(request, callback, ctx);
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
			debug("Set fp for "+ this + " to "+ pathFingerprint);
		}
		else
			this.pathFingerprint = null;
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
		scanState.debug("["+path.getUri()+"] "+ msg);
	}
	
	public void analyzePage(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzePage(request, response, this);
	}
	
	public void analyzeContent(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzePage(request, response, this);
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
		return scanState.allocateXssId();
	}
	public String createXssTag(int xssId) {
		return scanState.createXssTag(xssId);
	}
	
	public String createXssTag(String prefix, int xssId) {
		return scanState.createXssTag(prefix, xssId);
	}
	
	public void registerXssRequest(HttpUriRequest request, int xssId) {
		scanState.registerXssRequest(request, xssId);
	}
	
	public HttpUriRequest getXssRequest(int xssId, int scanId) {
		return scanState.getXssRequest(xssId, scanId);
	}
	
	public NameValuePair getFuzzableParameter() {
		return requestBuilder.getFuzzableParameter();
	}
	
	public void unlockChildren() {
		debug("Unlocking "+ this);
		synchronized(childStates) {
			setInitialChecksFinished();
			for(PathState c: childStates) {
				if(c.isLocked()) {
					final IHttpResponse r = c.getResponse();
					final HttpUriRequest request = c.createRequest();
					c.setUnlocked();
					switch(c.getPath().getPathType()) {
					case PATH_FILE:
						if(r != null) 
							fetchFileProcessor.processResponse(scanState.getCrawler(), request, r, createModuleContext());
						else
							c.submitRequest(request, fetchFileProcessor);
						break;
					case PATH_DIRECTORY:
						if(r != null) 
							fetchDirProcessor.processResponse(scanState.getCrawler(), request, r, createModuleContext());
						else
							c.submitRequest(request, fetchDirProcessor);
						break;
					case PATH_UNKNOWN:
						
						if(r != null) 
							fetchUnknownProcessor.processResponse(scanState.getCrawler(), request, r, createModuleContext());
						else
							c.submitRequest(request, fetchUnknownProcessor);
						break;
					}
				}
			}
		}
		
	}
	
	public String toString() {
		return "STATE: ["+ path.toString() + "]";
	}
	
	@Override
	public IModuleContext createModuleContext() {
		return new ModuleContext(scanState, requestBuilder, this);
	}
}
