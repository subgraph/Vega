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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.impl.scanner.handlers.DirectoryProcessor;
import com.subgraph.vega.impl.scanner.urls.ResponseAnalyzer;

public class PathStateManager {
	private final Logger logger = Logger.getLogger("scanner");
	private final IScannerConfig config;
	private List<IBasicModuleScript> injectionModules;
	private final IWorkspace workspace;
	private final IWebCrawler crawler;
	private final ResponseAnalyzer responseAnalyzer;
	private final boolean isProxyScan;
	private final ICrawlerResponseProcessor directoryFetchCallback = new DirectoryProcessor();
	private final Wordlists wordlists = new Wordlists();

	private final Map<IWebPath, PathState> modelToScanState = new HashMap<IWebPath, PathState>();

	private int currentXssId = 0;
	private final Map<Integer, HttpUriRequest> xssRequests = new HashMap<Integer, HttpUriRequest>();

	private final IScanInstance scanInstance;

	private Object progressLock = new Object();
	private volatile int totalPathCount;
	private volatile int completedPathCount;


	public PathStateManager(IScannerConfig config, List<IBasicModuleScript> injectionModules, IWorkspace workspace, IWebCrawler crawler, ResponseAnalyzer responseAnalyzer, IScanInstance scanInstance, boolean isProxyScan) {
		this.config = config;
		this.injectionModules = injectionModules;
		this.workspace = workspace;
		this.crawler = crawler;
		this.responseAnalyzer = responseAnalyzer;
		this.scanInstance = scanInstance;
		this.isProxyScan = isProxyScan;
	}

	public void setInjectionModules(List<IBasicModuleScript> injectionModules) {
		this.injectionModules = injectionModules;
	}

	public List<IBasicModuleScript> getInjectionModules() {
		return injectionModules;
	}

	public boolean requestLoggingEnabled() {
		return config.getLogAllRequests();
	}

	public boolean hasSeenPath(IWebPath path) {
		synchronized(modelToScanState) {
			return modelToScanState.containsKey(path);
		}
	}

	public boolean isProxyScan() {
		return isProxyScan;
	}

	/* called with modelToScanState lock */
	private PathState getParentDirectoryState(IWebPath path) {
		final IWebPath parentPath = path.getParentPath();
		if(parentPath == null)
			return null;
		if(!modelToScanState.containsKey(parentPath)) {
			if(parentPath.getPathType() != PathType.PATH_DIRECTORY)
				parentPath.setPathType(PathType.PATH_DIRECTORY);
			final PathState parentState = createStateForPathNoChecks(parentPath, directoryFetchCallback);
			modelToScanState.put(parentPath, parentState);
			return parentState;
		}
		return modelToScanState.get(parentPath);


	}

	public PathState createStateForPath(IWebPath path, ICrawlerResponseProcessor fetchCallback) {
		final PathState parent = getParentDirectoryState(path);
		if(parent != null) {
			if(hasExceededLimits(parent)) {
				logger.warning("Failed to add "+ path.getUri().toString() + " due to exceeded limits");
				return null;
			} else if(exceedsDuplicatePathLimit(path.getPathComponent(), parent)) {
				logger.warning("Maximum duplicate path limit of "+ config.getMaxDuplicatePaths() + " exceeded adding "+ path.getUri().toString());
				return null;
			}
		}
		return createStateForPathNoChecks(path, fetchCallback);
	}

	boolean hasExceededLimits(PathState ps) {
		if(ps.getDepth() > config.getMaxDepth()) {
			logger.warning("Maximum path depth of "+ config.getMaxDepth() + " exceeded at "+ ps.getPath().getUri().toString());
			return true;
		} else if(ps.getChildCount() > config.getMaxChildren()) {
			logger.warning("Maximum child path count of "+ config.getMaxChildren() + " exceeded at "+ ps.getPath().getUri().toString());
			return true;
		} else if(ps.getDescendantCount() > config.getMaxDescendants()) {
			logger.warning("Maximum total descendant count of "+ config.getMaxDescendants() + " exceeded at "+ ps.getPath().getUri().toString());
			return true;
		}
		return false;
	}

	private boolean exceedsDuplicatePathLimit(String name, PathState parent) {
		int count = 0;
		PathState ps = parent;
		while(ps != null) {
			if(ps.getPath().getPathComponent().equalsIgnoreCase(name)) {
				count += 1;
			}
			ps = ps.getParentState();
		}
		return count > config.getMaxDuplicatePaths();
	}

	private PathState createStateForPathNoChecks(IWebPath path, ICrawlerResponseProcessor fetchCallback) {
		synchronized(modelToScanState) {
			if(path == null)
				throw new NullPointerException();
			if(modelToScanState.containsKey(path))
				throw new IllegalStateException("Path already exists."+ path);
			final PathState parent = getParentDirectoryState(path);
			final PathState st = PathState.createBasicPathState(fetchCallback, this, parent, path);
			modelToScanState.put(path, st);
			return st;
		}
	}

	public PathState getStateForPath(IWebPath path) {
		if(path == null)
			return null;
		synchronized(modelToScanState) {
			return modelToScanState.get(path);
		}
	}

	public IWebCrawler getCrawler() {
		return crawler;
	}

	public void analyzePage(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		responseAnalyzer.analyzePage(ctx, request, response);
	}

	public void analyzeContent(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		responseAnalyzer.analyzeContent(ctx, request, response);
	}

	public void analyzePivot(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		responseAnalyzer.analyzePivot(ctx, request, response);
	}

	public String createXssTag(int xssId) {
		return createXssTag("", xssId);
	}
	
	public String createXssPattern(int xssId) {
		return createXssPattern("", xssId);
	}

	public int allocateXssId() {
		synchronized(xssRequests) {
			return currentXssId++;
		}
	}
	public String createXssTag(String prefix, int xssId) {
		if(scanInstance.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
			return formatXssTag(prefix, xssId, 0);
		} else {
			return formatXssTag(prefix, xssId, scanInstance.getScanId());
		}
	}
	
	public String createXssPattern(String prefix, int xssId) {
		if(scanInstance.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
			return formatXssPattern(prefix, xssId, 0);
		} else {
			return formatXssPattern(prefix, xssId, scanInstance.getScanId());
		}		
	}

	private String formatXssTag(String prefix, int xssId, long scanId) {
		return String.format("%s-->\">'>'\"<vvv%06dv%06d>", prefix, xssId, scanId);
	}

	private String formatXssPattern(String prefix, int xssId, long scanId) {
		return String.format("%svvv%06dv%06d", prefix, xssId, scanId);
	}
	
	public void registerXssRequest(HttpUriRequest request, int xssId) {
		synchronized(xssRequests) {
			xssRequests.put(xssId, request);
		}
	}
	
	public long getScanId() {
		return scanInstance.getScanId();
	}

	public HttpUriRequest getXssRequest(int xssId, int scanId) {
		synchronized(xssRequests) {
			if(isValidXssId(xssId, scanId)) {
				return xssRequests.get(xssId);
			} else {
				return null;
			}
		}
	}
	
	private boolean isValidXssId(int xssId, int scanId) {
		if(scanId == 0 && scanInstance.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID && xssId < currentXssId) {
			return true;
		} else {
			return scanId == scanInstance.getScanId() && xssId < currentXssId;
		}
	}

	public IRequestLog getRequestLog() {
		return workspace.getRequestLog();
	}

	public IScanInstance getScanInstance() {
		return scanInstance;
	}

	public boolean isExcludedParameter(String name) {
		return config.getExcludedParameterNames().contains(name.toLowerCase());
	}
	
	public void debug(String message) {
		if(config.getDisplayDebugOutput())
			logger.info(message);
	}
	
	public void debug(String message, Boolean colored) {
		if(config.getDisplayDebugOutput()) {
			if (colored) {
				logger.finer(message);
			} else {
				logger.info(message);
			}
		}
	}
	
	public void reportRequestException(HttpUriRequest request, Throwable ex) {
		logger.warning("Exception processing request: "+ request +" : "+ ex.getMessage());
		scanInstance.notifyScanException(request, ex);
	}

	public List<String> getFileExtensionList() {
		return wordlists.getFileExtensions();
	}

	public IContentAnalyzer getContentAnalyzer() {
		return responseAnalyzer.getContentAnalyzer();
	}

	public boolean getDirectoryInjectionChecksFlag() {
		return config.getDirectoryInjectionChecksFlag();
	}

	public boolean getNonParameterFileInjectionChecksFlag() {
		return config.getNonParameterFileInjectionChecksFlag();
	}
	
	public int getMaxParameterCount() {
		return config.getMaxParameterCount();
	}
	
	void notifyPathNodeStart(PathState ps) {
		final String path = pathNodeToPathString(ps);
		debug("Starting path "+ path);
		synchronized(progressLock) {
			if(totalPathCount == 0) {
				scanInstance.updateScanProgress(path, 0, 1);
			}
			totalPathCount += 1;
		}
		scanInstance.updateScanProgress(completedPathCount, totalPathCount);
	}
	
	void notifyPathNodeFinish(PathState ps) {
		final String path = pathNodeToPathString(ps);
		debug("Finished path "+ path);
		synchronized (progressLock) {
			completedPathCount += 1;
		}
		scanInstance.updateScanProgress(path, completedPathCount, totalPathCount);
	}
	
	private String pathNodeToPathString(PathState ps) {
		final URI uri = ps.getPath().getUri();
		return uri.toString();
	}
}
