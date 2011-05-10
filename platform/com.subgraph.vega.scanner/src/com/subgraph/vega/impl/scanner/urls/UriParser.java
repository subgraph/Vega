package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.handlers.DirectoryProcessor;
import com.subgraph.vega.impl.scanner.handlers.FileProcessor;
import com.subgraph.vega.impl.scanner.handlers.UnknownProcessor;
import com.subgraph.vega.impl.scanner.state.PathState;
import com.subgraph.vega.impl.scanner.state.PathStateManager;

public class UriParser {
	private final IWorkspace workspace;
	private final ICrawlerResponseProcessor directoryProcessor;
	private final ICrawlerResponseProcessor fileProcessor;
	private final ICrawlerResponseProcessor unknownProcessor;
	private final PathStateManager pathStateManager;

	public UriParser(IScannerConfig config, IScannerModuleRegistry moduleRegistry, IWorkspace workspace, IWebCrawler crawler, UriFilter filter, IContentAnalyzer contentAnalyzer, IScanInstance scanInstance) {
		this.workspace = workspace;
		this.directoryProcessor = new DirectoryProcessor();
		this.fileProcessor = new FileProcessor();
		this.unknownProcessor = new UnknownProcessor();
		this.pathStateManager = new PathStateManager(config, moduleRegistry, workspace, crawler, new ResponseAnalyzer(config, contentAnalyzer, this, filter), scanInstance);
	}

	public IPathState processUri(URI uri) {
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
				}
				processPath(childPath, uri, (i == (parts.length - 1)), hasTrailingSlash);
			}
			path = childPath;
		}
		return pathStateManager.getStateForPath(path);
	}

	private IWebHost getWebHost(HttpHost host) {
		IWebHost webHost = null;
		synchronized(workspace) {
			final IWebModel webModel = workspace.getWebModel();
			webHost = webModel.getWebHostByHttpHost(host);
			if(webHost == null)
				webHost = webModel.createWebHostFromHttpHost(host);
		}
		processWebHost(webHost);
		return webHost;
	}

	private void processWebHost(IWebHost webHost) {
		final IWebPath rootPath = webHost.getRootPath();
		synchronized(pathStateManager) {
			if(!pathStateManager.hasSeenPath(rootPath)) {
				pathStateManager.createStateForPath(rootPath, directoryProcessor);
			}
		}
	}

	private void processPath(IWebPath webPath, URI uri, boolean isLast, boolean hasTrailingSlash) {
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
			if(!pathStateManager.hasSeenPath(webPath)) {
				webPath.setPathType(PathType.PATH_DIRECTORY);
				pathStateManager.createStateForPath(webPath, directoryProcessor);
				return;
			}
			if(webPath.getPathType() != PathType.PATH_DIRECTORY) {
				// XXX What to do in this case?  The PathState node may be executing PATH_UNKNOWN checks already
				System.out.println("Scan state node for path="+ webPath + " already exists but it's not a directory in UriParser.processDirectory()");
			}
		}
	}

	private void processPathWithQuery(IWebPath path, URI uri) {
		path.setPathType(PathType.PATH_FILE);
		List<NameValuePair> plist = URLEncodedUtils.parse(uri, "UTF-8");
		synchronized(pathStateManager) {
			getPathStateForFile(path).maybeAddParameters(plist);
		}
	}

	private PathState getPathStateForFile(IWebPath filePath) {
		synchronized(pathStateManager) {
			if(pathStateManager.hasSeenPath(filePath))
				return pathStateManager.getStateForPath(filePath);
			else
				return pathStateManager.createStateForPath(filePath, fileProcessor);
		}
	}

	private void processUnknown(IWebPath path) {
		synchronized(pathStateManager) {
			if(pathStateManager.hasSeenPath(path))
				return;
			pathStateManager.createStateForPath(path, unknownProcessor);
		}
	}

}
