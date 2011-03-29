package com.subgraph.vega.impl.scanner.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlertModel;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.urls.PageAnalyzer;
import com.subgraph.vega.impl.scanner.urls.PivotAnalyzer;

public class PathStateManager {
	private final Logger logger = Logger.getLogger("scanner");
	private final IScannerModuleRegistry moduleRegistry;
	private final IWorkspace workspace;
	private final IWebCrawler crawler;
	private final PageAnalyzer pageAnalyzer;
	private final IContentAnalyzer contentAnalyzer;
	private final PivotAnalyzer pivotAnalyzer;
	private boolean logAllRequests = true;
	
	private final Map<IWebPath, PathState> modelToScanState = new HashMap<IWebPath, PathState>();
	
	private final Map<IWebPath, PathStateParameterManager> pathToParameterScanState = new HashMap<IWebPath, PathStateParameterManager>();

	private int currentXssId = 0;
	private Map<Integer, HttpUriRequest> xssRequests = new HashMap<Integer, HttpUriRequest>();

	private final int scanId;

	public PathStateManager(IScannerModuleRegistry moduleRegistry, IWorkspace workspace, IWebCrawler crawler, PageAnalyzer pageAnalyzer, IContentAnalyzer contentAnalyzer, PivotAnalyzer pivotAnalyzer) {
		this.moduleRegistry = moduleRegistry;
		this.workspace = workspace;
		this.crawler = crawler;
		this.pageAnalyzer = pageAnalyzer;
		this.contentAnalyzer = contentAnalyzer;
		this.pivotAnalyzer = pivotAnalyzer;
		
		final Random r = new Random();
		scanId = r.nextInt(999999) + 1; 
	}
	
	public IScannerModuleRegistry getModuleRegistry() {
		return moduleRegistry;
	}
	
	public boolean requestLoggingEnabled() {
		return logAllRequests;
	}
	
	public boolean hasSeenPath(IWebPath path) {
		synchronized(modelToScanState) {
			return modelToScanState.containsKey(path);
		}
	}
	public PathState getStateForPath(IWebPath path) {
		if(path == null)
			return null;
		synchronized(modelToScanState) {
			if(modelToScanState.containsKey(path))
				return modelToScanState.get(path);
			final PathState parentState = getStateForPath(path.getParentPath());
			final PathState st = new PathState(this, parentState, path);
			if(parentState != null) 
				parentState.addChildState(st);
			
			modelToScanState.put(path, st);
			return st;
		}
	}
	
	public List<PathState> getStatesForPathAndParameters(IWebPath path, List<NameValuePair> parameters) {
		return getParameterManagerForPath(path).getStatesForParameterList(parameters);
	}
	
	public List<PathState> createStatesForPathAndParameters(IWebPath path, List<NameValuePair> parameters) {
		return getParameterManagerForPath(path).addParameterList(this, path, parameters);
	}
	
	public boolean hasParametersForPath(IWebPath path, List<NameValuePair> parameters) {
		return getParameterManagerForPath(path).hasParameterList(parameters);
	}
	
	private PathStateParameterManager getParameterManagerForPath(IWebPath path) {
		synchronized (pathToParameterScanState) {
			if(!pathToParameterScanState.containsKey(path))
				pathToParameterScanState.put(path, new PathStateParameterManager(this, path));
			return pathToParameterScanState.get(path);
		}
	}

	public IWebCrawler getCrawler() {
		return crawler;
	}
	
	public void analyzePage(HttpUriRequest request, IHttpResponse response, IPathState pathState) {
		pageAnalyzer.analyzePage(request, response, pathState);
	}
	
	public void analyzeContent(HttpUriRequest request, IHttpResponse response, IPathState pathState) {
		contentAnalyzer.processResponse(response, false, false);
	}
	
	public void analyzePivot(HttpUriRequest request, IHttpResponse response, IPathState pathState) {
		pivotAnalyzer.analyze(request, response, pathState);
	}
	
	public String createXssTag(int xssId) {
		return createXssTag("", xssId);
	}
	
	public int allocateXssId() {
		synchronized(xssRequests) {
			return currentXssId++;
		}
	}
	public String createXssTag(String prefix, int xssId) {
		return String.format("%s-->\">'>'\"<vvv%06dv%06d>", prefix, xssId, scanId);
	}
	
	public void registerXssRequest(HttpUriRequest request, int xssId) {
		synchronized(xssRequests) {
			xssRequests.put(xssId, request);
		}
	}
	
	public HttpUriRequest getXssRequest(int xssId, int scanId) {
		synchronized(xssRequests) {
			if(scanId == this.scanId && xssId < currentXssId)
				return xssRequests.get(xssId);
			else 
				return null;
		}
	}
	
	public IRequestLog getRequestLog() {
		return workspace.getRequestLog();
	}

	public IScanAlertModel getScanAlertModel() {
		return workspace.getScanAlertModel();
	}

	public void debug(String message) {
		logger.info(message);
	}
}
