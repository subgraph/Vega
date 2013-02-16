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
package com.subgraph.vega.internal.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpRequest;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerResult;
import com.subgraph.vega.api.analysis.MimeType;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.util.VegaURI;
import com.subgraph.vega.internal.analysis.urls.UrlExtractor;

public class ContentAnalyzer implements IContentAnalyzer {
	
	private final Logger logger = Logger.getLogger("analysis");
	
	private final IScanInstance scanInstance;
	private final ContentAnalyzerFactory factory;
	private final UrlExtractor urlExtractor = new UrlExtractor();
	private final MimeDetector mimeDetector = new MimeDetector();

	private final Object responseProcessingLock = new Object();

	private List<IResponseProcessingModule> responseProcessingModules;
	private boolean addLinksToModel;
	private boolean defaultAddToRequestLog;
		
	ContentAnalyzer(ContentAnalyzerFactory factory, IScanInstance scanInstance) {
		this.factory = factory;
		this.scanInstance = scanInstance;
		this.addLinksToModel = true;
		this.defaultAddToRequestLog = true;
	}

	@Override
	public IContentAnalyzerResult processResponse(IHttpResponse response) {
		return processResponse(response, defaultAddToRequestLog, true);
	}

	@Override
	public void setDefaultAddToRequestLog(boolean flag) {
		defaultAddToRequestLog = flag;		
	}

	@Override
	public IContentAnalyzerResult processResponse(IHttpResponse response, boolean addToRequestLog, boolean scrapePage) {
		final ContentAnalyzerResult result = new ContentAnalyzerResult();
		if(response == null) {
			logger.warning("ContentAnalyzer.processResponse() called with null response");
			return result;
		}

		final IWorkspace workspace = factory.getCurrentWorkspace();
		if(workspace == null) {
			logger.warning("ContentAnalyzer.processResponse() called while no workspace is active");
			return result;
		}
		
		if(addToRequestLog) { 
			workspace.getRequestLog().addRequestResponse(response);
		}

		final VegaURI uri = VegaURI.fromHostAndRequest(response.getHost(), response.getOriginalRequest());
		final IWebPath path = workspace.getWebModel().getWebPathByUri(uri);
		path.setVisited(true);
		
		result.setDeclaredMimeType(mimeDetector.getDeclaredMimeType(response));
		result.setSniffedMimeType(mimeDetector.getSniffedMimeType(response));
		
		final String mimeType = getBestMimeType(result);
		if(mimeType != null && path.getMimeType() == null) {
			path.setMimeType(mimeType);
		}
		
		if(scrapePage)
			runExtractUrls(result, response, workspace.getWebModel());
		runResponseProcessingModules(response.getOriginalRequest(), response, result.getDeclaredMimeType(), result.getSniffedMimeType(), workspace);
		return result;
	}
	
	private String getBestMimeType(IContentAnalyzerResult result) {
		if(result.getSniffedMimeType() != MimeType.MIME_NONE) {
			return result.getSniffedMimeType().getCanonicalName();
		} else if(result.getDeclaredMimeType() != MimeType.MIME_NONE) {
			return result.getDeclaredMimeType().getCanonicalName();
		} else {
			return null;
		}
	}

	@Override
	public void setResponseProcessingModules(List<IResponseProcessingModule> modules) {
		responseProcessingModules = new ArrayList<IResponseProcessingModule>(modules);
	}
	
	private void runExtractUrls(ContentAnalyzerResult result, IHttpResponse response, IWebModel webModel) {
		if(response.isMostlyAscii()) {
			for(VegaURI u : urlExtractor.findUrls(response)) {
				if(addLinksToModel && (schemeEquals(u, "http") || schemeEquals(u, "https")))
					webModel.getWebPathByUri(u);
				result.addUri(u);
			}
		}
	}
	
	private boolean schemeEquals(VegaURI uri, String scheme) {
		final String s = uri.getTargetHost().getSchemeName();
		return s.equalsIgnoreCase(scheme);
	}
	
	private void runResponseProcessingModules(HttpRequest request, IHttpResponse response, MimeType declaredMime, MimeType sniffedMime, IWorkspace workspace) {
		if(responseProcessingModules == null || !response.isMostlyAscii()) {
			return;
		}

		if(!(isDefaultResponseProcessingMimetype(declaredMime) || isDefaultResponseProcessingMimetype(sniffedMime))) {
			return;
		}
		
		synchronized (responseProcessingLock) {
			for(IResponseProcessingModule m: responseProcessingModules) {
				if(m.isEnabled()) {
					m.processResponse(scanInstance, request, response, workspace);
				}
			}
		}
	}

	private boolean isDefaultResponseProcessingMimetype(MimeType mime) {
		final String name = mime.getCanonicalName();
		return (name.contains("text") || name.contains("javascript") || name.contains("xml"));
	}

	@Override
	public void setAddLinksToModel(boolean flag) {
		addLinksToModel = flag;
	}
}
