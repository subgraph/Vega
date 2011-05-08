package com.subgraph.vega.impl.scanner.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlertModel;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.handlers.DirectoryProcessor;
import com.subgraph.vega.impl.scanner.urls.ResponseAnalyzer;

public class PathStateManager {
	private final Logger logger = Logger.getLogger("scanner");
	private final IScannerConfig config;
	private final IScannerModuleRegistry moduleRegistry;
	private final IWorkspace workspace;
	private final IWebCrawler crawler;
	private final ResponseAnalyzer responseAnalyzer;
	private final ICrawlerResponseProcessor directoryFetchCallback = new DirectoryProcessor();
	private final Wordlists wordlists = new Wordlists();

	private final Map<IWebPath, PathState> modelToScanState = new HashMap<IWebPath, PathState>();

	private int currentXssId = 0;
	private final Map<Integer, HttpUriRequest> xssRequests = new HashMap<Integer, HttpUriRequest>();

	private final int scanId;

	public PathStateManager(IScannerConfig config, IScannerModuleRegistry moduleRegistry, IWorkspace workspace, IWebCrawler crawler, ResponseAnalyzer responseAnalyzer) {
		this.config = config;
		this.moduleRegistry = moduleRegistry;
		this.workspace = workspace;
		this.crawler = crawler;
		this.responseAnalyzer = responseAnalyzer;
		final Random r = new Random();
		scanId = r.nextInt(999999) + 1;
	}

	public int getScanId() {
		return scanId;
	}

	public IScannerModuleRegistry getModuleRegistry() {
		return moduleRegistry;
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
			final PathState parentState = createStateForPath(parentPath, directoryFetchCallback);
			modelToScanState.put(parentPath, parentState);
			return parentState;
		}
		return modelToScanState.get(parentPath);


	}
	public PathState createStateForPath(IWebPath path, ICrawlerResponseProcessor fetchCallback) {
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

	public void analyzePage(IModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		responseAnalyzer.analyzePage(ctx, request, response);
	}

	public void analyzeContent(IModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		responseAnalyzer.analyzeContent(ctx, request, response);
	}

	public void analyzePivot(IModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
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
}
