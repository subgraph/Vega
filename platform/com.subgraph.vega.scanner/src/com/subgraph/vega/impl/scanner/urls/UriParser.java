package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.impl.scanner.handlers.DirectoryProcessor;
import com.subgraph.vega.impl.scanner.handlers.FileProcessor;
import com.subgraph.vega.impl.scanner.handlers.UnknownProcessor;
import com.subgraph.vega.impl.scanner.state.PathState;
import com.subgraph.vega.impl.scanner.state.PathStateManager;
import com.subgraph.vega.urls.IUrlExtractor;

public class UriParser {
	private final IWorkspace workspace;
	private final PageAnalyzer pageAnalyzer;
	private final ICrawlerResponseProcessor directoryProcessor;
	private final ICrawlerResponseProcessor fileProcessor;
	private final ICrawlerResponseProcessor unknownProcessor;
	private final PathStateManager pathStateManager;
	
	public UriParser(List<IResponseProcessingModule> responseModules, IWorkspace workspace, IWebCrawler crawler, UriFilter filter, IUrlExtractor urlExtractor) {
		this.workspace = workspace;
		
		this.pageAnalyzer = new PageAnalyzer(workspace.getWebModel(), filter, urlExtractor, this);
		this.directoryProcessor = new DirectoryProcessor();
		this.fileProcessor = new FileProcessor();
		this.unknownProcessor = new UnknownProcessor();
		this.pathStateManager = new PathStateManager(workspace, crawler, pageAnalyzer, new ContentAnalyzer(responseModules, workspace), new PivotAnalyzer());
	}
	
	public void processUri(URI uri) {
		final HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
		final IWebHost webHost = getWebHost(host);
		IWebPath path = webHost.getRootPath();
		final boolean hasTrailingSlash = uri.getPath().endsWith("/");
		
		String[] parts = uri.getPath().split("/");
		IWebPath childPath;
		for(int i = 1; i < parts.length; i++) {
			synchronized(path) {
				childPath = path.getChildPath(parts[i]);
				if(childPath == null) {
					childPath = path.addChildPath(parts[i]);
					processNewPath(childPath, uri, (i == (parts.length - 1)), hasTrailingSlash);
				}
			}
			path = childPath;
		}
	}
	
	private IWebHost getWebHost(HttpHost host) {
		IWebHost webHost = null;
		synchronized(workspace) {
			final IWebModel webModel = workspace.getWebModel();
			webHost = webModel.getWebHostByHttpHost(host);
			if(webHost != null)
				return webHost;
			webHost = webModel.createWebHostFromHttpHost(host);			
		}
		processNewWebHost(webHost);
		return webHost;
	}
	
	private void processNewWebHost(IWebHost webHost) {
		final IWebPath rootPath = webHost.getRootPath();
		synchronized(pathStateManager) {
			if(!pathStateManager.hasSeenPath(rootPath)) {
				final PathState ps = pathStateManager.getStateForPath(rootPath);
				ps.submitRequest(directoryProcessor);
			}
		}
	}
	
	private void processNewPath(IWebPath webPath, URI uri, boolean isLast, boolean hasTrailingSlash) {
		if(!isLast || (isLast && hasTrailingSlash)) {
			processDirectory(webPath);
		} else if(uri.getQuery() != null) {
			processPathWithQuery(webPath, uri);
		} else {
			processUnknown(webPath);
		}	
	}
	
	private void processDirectory(IWebPath webPath) {
		synchronized(pathStateManager) {
			if(pathStateManager.hasSeenPath(webPath) && webPath.getPathType() == PathType.PATH_DIRECTORY)
				return;
			webPath.setPathType(PathType.PATH_DIRECTORY);
			final PathState ps = pathStateManager.getStateForPath(webPath);
			maybeSubmit(ps, directoryProcessor);
		}
	}
	
	private void processPathWithQuery(IWebPath path, URI uri) {
		path.setPathType(PathType.PATH_FILE);
		List<NameValuePair> plist = URLEncodedUtils.parse(uri, "UTF-8");
		List<PathState> newStates = getNewPathStatesForParameters(path, plist);
		if(newStates == null)
			return;
		for(PathState ps : newStates)  {
			maybeSubmit(ps, fileProcessor);
		}
	}
	
	private List<PathState> getNewPathStatesForParameters(IWebPath path, List<NameValuePair> plist) {
		synchronized (pathStateManager) {
			if(pathStateManager.hasParametersForPath(path, plist))
				return null;
			return pathStateManager.createStatesForPathAndParameters(path, plist);
		}
	}
	
	private void processUnknown(IWebPath path) {
		synchronized(pathStateManager) {
			if(pathStateManager.hasSeenPath(path))
				return;
			final PathState ps = pathStateManager.getStateForPath(path);
			maybeSubmit(ps, unknownProcessor);
		}
	}
	
	private void maybeSubmit(PathState ps, ICrawlerResponseProcessor callback) {
		final PathState pps = ps.getParentState();
		if(pps == null || pps.getInitialChecksFinished())
			ps.submitRequest(callback);
		else
			ps.setLocked();
	}
		
		
}
