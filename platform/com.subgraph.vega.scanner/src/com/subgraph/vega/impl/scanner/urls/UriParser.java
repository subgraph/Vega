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
package com.subgraph.vega.impl.scanner.urls;

import java.util.List;

import org.apache.http.Consts;
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
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.util.VegaURI;
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

	public UriParser(IScannerConfig config, List<IBasicModuleScript> injectionModules, IWorkspace workspace, IWebCrawler crawler, UriFilter filter, IContentAnalyzer contentAnalyzer, IScanInstance scanInstance, boolean isProxyScan) {
		this.workspace = workspace;
		this.directoryProcessor = new DirectoryProcessor();
		this.fileProcessor = new FileProcessor();
		this.unknownProcessor = new UnknownProcessor();
		this.pathStateManager = new PathStateManager(config, injectionModules, workspace, crawler, new ResponseAnalyzer(config, contentAnalyzer, this, filter, isProxyScan), scanInstance, isProxyScan);
	}

	public void updateInjectionModules(List<IBasicModuleScript> injectionModules) {
		pathStateManager.setInjectionModules(injectionModules);
	}

	public IPathState processUri(VegaURI uri) {
		final IWebHost webHost = getWebHost(uri.getTargetHost());
		final IWebPath rootPath = webHost.getRootPath();
		IWebPath path = rootPath;
		final boolean hasTrailingSlash = uri.getPath().endsWith("/");

		String[] parts = uri.getPath().split("/");
		IWebPath childPath = null;
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
		
		if(path == rootPath && uri.getQuery() != null) {
			final PathState ps = pathStateManager.getStateForPath(rootPath);
			synchronized (pathStateManager) {
				ps.maybeAddParameters(URLEncodedUtils.parse(uri.getQuery(), Consts.UTF_8));
			}
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
				pathStateManager.createStateForPath(rootPath, unknownProcessor);
			}
		}
	}

	private void processPath(IWebPath webPath, VegaURI uri, boolean isLast, boolean hasTrailingSlash) {
		if(!isLast || (isLast && hasTrailingSlash)) {
			processDirectory(webPath, uri);
		} else if(uri.getQuery() != null) {
			webPath.setPathType(PathType.PATH_FILE);
			processPathWithQuery(webPath, uri);
		} else {
			processUnknown(webPath);
		}
	}

	private void processDirectory(IWebPath webPath, VegaURI uri) {
		synchronized(pathStateManager) {
			if(!pathStateManager.hasSeenPath(webPath)) {
				webPath.setPathType(PathType.PATH_DIRECTORY);
				final PathState ps = pathStateManager.createStateForPath(webPath, directoryProcessor);
				if(uri.getQuery() != null) {
					ps.maybeAddParameters(URLEncodedUtils.parse(uri.getQuery(), Consts.UTF_8));
				}
				return;
			}
			if(webPath.getPathType() != PathType.PATH_DIRECTORY) {
				// XXX What to do in this case?  The PathState node may be executing PATH_UNKNOWN checks already
				System.out.println("Scan state node for path="+ webPath + " already exists but it's not a directory in UriParser.processDirectory()");
			}
		}
	}


	private void processPathWithQuery(IWebPath path, VegaURI uri) {
		path.setPathType(PathType.PATH_FILE);
		List<NameValuePair> plist = URLEncodedUtils.parse(uri.getQuery(), Consts.UTF_8);
		synchronized(pathStateManager) {
			final PathState ps = getPathStateForFile(path);
			if(ps != null) {
				ps.maybeAddParameters(plist);
			}
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
