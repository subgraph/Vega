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
	private final List<IBasicModuleScript> injectionModules;
	private final IWorkspace workspace;
	private final IWebCrawler crawler;
	private final ResponseAnalyzer responseAnalyzer;
	private final ICrawlerResponseProcessor directoryFetchCallback = new DirectoryProcessor();
	private final Wordlists wordlists = new Wordlists();

	private final Map<IWebPath, PathState> modelToScanState = new HashMap<IWebPath, PathState>();

	private int currentXssId = 0;
	private final Map<Integer, HttpUriRequest> xssRequests = new HashMap<Integer, HttpUriRequest>();

	private final IScanInstance scanInstance;

	public PathStateManager(IScannerConfig config, List<IBasicModuleScript> injectionModules, IWorkspace workspace, IWebCrawler crawler, ResponseAnalyzer responseAnalyzer, IScanInstance scanInstance) {
		this.config = config;
		this.injectionModules = injectionModules;
		this.workspace = workspace;
		this.crawler = crawler;
		this.responseAnalyzer = responseAnalyzer;
		this.scanInstance = scanInstance;
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

	public int allocateXssId() {
		synchronized(xssRequests) {
			return currentXssId++;
		}
	}
	public String createXssTag(String prefix, int xssId) {
		return String.format("%s-->\">'>'\"<vvv%06dv%06d>", prefix, xssId, scanInstance.getScanId());
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
			if(scanId == scanInstance.getScanId() && xssId < currentXssId)
				return xssRequests.get(xssId);
			else
				return null;
		}
	}

	public IRequestLog getRequestLog() {
		return workspace.getRequestLog();
	}

	public IScanInstance getScanInstance() {
		return scanInstance;
	}

	public void debug(String message) {
		if(config.getDisplayDebugOutput())
			logger.info(message);
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
}
