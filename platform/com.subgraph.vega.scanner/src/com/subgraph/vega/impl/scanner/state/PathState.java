package com.subgraph.vega.impl.scanner.state;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.handlers.DirectoryProcessor;
import com.subgraph.vega.impl.scanner.handlers.FileProcessor;
import com.subgraph.vega.impl.scanner.handlers.UnknownProcessor;

public class PathState {
	
	private final static int MAX_MISC_ENTRIES = 10;
	
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
	private PageFingerprint pathFingerprint;
	private PageFingerprint unknownFingerprint;
	
	private boolean isDone;
	
	private boolean isSureDirectory;
	private boolean isPageMissing;
	private boolean isBogusParameter;
	private boolean responseVaries;
	private int miscCount;
	private int ognlCount;
	private boolean lockedFlag;
	private boolean initialChecksFinished;
	private boolean injectionSkip[] = new boolean[15];
	private int injectionSkipAdd = 0;
	private HttpUriRequest[] miscRequests = new HttpUriRequest[MAX_MISC_ENTRIES];
	private IHttpResponse[] miscResponses = new IHttpResponse[MAX_MISC_ENTRIES];
	private PageFingerprint[] miscFingerprints = new PageFingerprint[MAX_MISC_ENTRIES];
	
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
	
	public void addChildState(PathState state) {
		synchronized(childStates) {
			for(PathState cs: childStates) {
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
	
	public synchronized void incrementOgnlCount() {
		ognlCount += 1;
	}
	
	public synchronized int getOgnlCount() {
		return ognlCount;
	}
	
	public void setUnknownFingerprint(PageFingerprint fp) {
		unknownFingerprint = fp;
	}
	
	public PageFingerprint getUnknownFingerprint() {
		return unknownFingerprint;
	}
	public void submitRequest(ICrawlerResponseProcessor callback) {
		submitRequest(callback, 0);
	}
	
	public void submitRequest(ICrawlerResponseProcessor callback, int flag) {
		final HttpUriRequest req = requestBuilder.createGetRequest();
		if(req != null)
			submitRequest(req, callback, flag);
	}
	
	public void submitAlteredRequest(ICrawlerResponseProcessor callback, String value) {
		submitAlteredRequest(callback, value, false, 0);
	}
	
	public void submitAlteredRequest(ICrawlerResponseProcessor callback, String value, int flag) {
		submitAlteredRequest(callback, value, false, flag);
	}
	
	public void submitAlteredRequest(ICrawlerResponseProcessor callback, String value, boolean append, int flag) {
		final HttpUriRequest req = requestBuilder.createAlteredRequest(value, append);
		if(req != null)
			submitRequest(req, callback, flag);
	}
	
	public void submitAlteredParameterNameRequest(ICrawlerResponseProcessor callback, String name, int flag) {
		 final HttpUriRequest req = requestBuilder.createAlteredParameterNameRequest(name);
		 if(req != null)
			 submitRequest(req, callback, flag);
	}

	public void submitMultipleAlteredRequests(ICrawlerResponseProcessor callback, String[] injectables) {
		submitMultipleAlteredRequests(callback, injectables, false);
	}
	
	public void submitMultipleAlteredRequests(ICrawlerResponseProcessor callback, String[] injectables, boolean append) {
		for(int i = 0; i < injectables.length; i++) 
			submitAlteredRequest(callback, injectables[i], append, i);		
	}
		
	public void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback) {
		submitRequest(request, callback, 0);
	}
	
	public void submitRequest(HttpUriRequest request, ICrawlerResponseProcessor callback, int flag) {
		scanState.getCrawler().submitTask(request, callback, new ScanRequestData(this, flag));
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
	
	public boolean add404Fingerprint(PageFingerprint fp) {
		return state404.add404Fingerprint(fp);
	}
	
	public boolean isRootPath() {
		return path.getParentPath() == null;
	}
	
	public boolean has404Fingerprints() {
		return state404.has404Fingerprints();
	}
	
	public PathState get404Parent() {
		return state404.get404Parent();
	}
	
	public boolean has404FingerprintMatching(PageFingerprint fp) {
		return state404.has404FingerprintMatching(fp);
	}
	public boolean hasParent404Fingerprint(PageFingerprint fp) {
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
			this.pathFingerprint = PageFingerprint.generateFromCodeAndString(response.getResponseCode(), response.getBodyAsString());
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
	public void error(HttpUriRequest request, IHttpResponse response, String message) {
		// XXX
		System.out.println("ERROR: "+ message);
		
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
	
	public synchronized int incrementMiscCount() {
		final int n = miscCount + 1;
		miscCount = n;
		return n;
	}
	public void setInjectSkipFlag(int index) {
		index += injectionSkipAdd;
		if(index >= 0 && index < injectionSkip.length)
			injectionSkip[index] = true;
	}
	
	public synchronized void setInjectSkipAdd(int n) {
		injectionSkipAdd = n;
	}
	
	public synchronized boolean getInjectSkipFlag(int index) {
		index += injectionSkipAdd;
		if(index >= 0 && index < injectionSkip.length)
			return injectionSkip[index];
		else
			return false;
	}
	
	private boolean isValidMiscIndex(int index) {
		return index >= 0 && index < MAX_MISC_ENTRIES;
	}
	
	public void addMiscRequestResponse(int index, HttpUriRequest request, IHttpResponse response) {
		if(isValidMiscIndex(index)) {
			synchronized(miscRequests) {
				miscRequests[index] = request;
				miscResponses[index] = response;
				miscFingerprints[index] = PageFingerprint.generateFromCodeAndString(response.getResponseCode(), response.getBodyAsString());
			}
		}
	}
	
	public HttpUriRequest getMiscRequest(int index) {
		if(isValidMiscIndex(index))
			synchronized(miscRequests) {
				return miscRequests[index];
			}
		else
			return null;
	}
	
	public IHttpResponse getMiscResponse(int index) {
		if(isValidMiscIndex(index))
			synchronized(miscRequests) {
				return miscResponses[index];
			}
		else
			return null;
	}
	
	public PageFingerprint getMiscFingerprint(int index) {
		if(isValidMiscIndex(index))
			synchronized(miscRequests) {
				return miscFingerprints[index];
			}
		else
			return null;			                    
	}
	
	public boolean miscFingerprintsMatch(int i1, int i2) {
		PageFingerprint fp1 = getMiscFingerprint(i1);
		PageFingerprint fp2 = getMiscFingerprint(i2);
		if(fp1 == null || fp2 == null)
			return false;
		
		return fp1.isSame(fp2);
	}
	
	public boolean miscFingerprintMatchesPath(int i) {
		PageFingerprint fp = getMiscFingerprint(i);
		if(fp == null) {
			debug("No fingerprint for index "+ i + " on " + path.getUri());
			return false;
		}
		return fp.isSame(pathFingerprint);
	}
	
	public PageFingerprint getPathFingerprint() {
		return pathFingerprint;
	}
	
	public boolean matchesPathFingerprint(PageFingerprint fp) {
		if(pathFingerprint == null) {
			debug("Whoops no path fingerprint for "+ path.getUri() + " : " + this);
			return false;
		}
		return pathFingerprint.isSame(fp);
	}
	
	public int getMiscResponseCode(int i) {
		final IHttpResponse response = getMiscResponse(i);
		if(response == null)
			return 0;
		else
			return response.getResponseCode();
	}
	
	public void resetMiscData() {
		synchronized(miscRequests) {
			for(int i = 0; i < miscRequests.length; i++) {
				miscRequests[i] = null;
				miscResponses[i] = null;
				miscFingerprints[i] = null;
			}
			miscCount = 0;
		}
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
	
	public void miscResponseChecks(int index) {
		if(isValidMiscIndex(index))
			responseChecks(getMiscRequest(index), getMiscResponse(index));
	}
	
	public void pivotChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzePivot(request, response, this);
		scanState.analyzeContent(request, response, this);
		scanState.analyzePage(request, response, this);
	}
	public void responseChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzeContent(request, response, this);
		scanState.analyzePage(request, response, this);
	}
	
	public void contentChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzeContent(request, response, this);
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
							fetchFileProcessor.processResponse(scanState.getCrawler(), request, r, new ScanRequestData(c, 0));
						else
							c.submitRequest(request, fetchFileProcessor);
						break;
					case PATH_DIRECTORY:
						if(r != null) 
							fetchDirProcessor.processResponse(scanState.getCrawler(), request, r, new ScanRequestData(c, 0));
						else
							c.submitRequest(request, fetchDirProcessor);
						break;
					case PATH_UNKNOWN:
						
						if(r != null) 
							fetchUnknownProcessor.processResponse(scanState.getCrawler(), request, r, new ScanRequestData(c, 0));
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
}
