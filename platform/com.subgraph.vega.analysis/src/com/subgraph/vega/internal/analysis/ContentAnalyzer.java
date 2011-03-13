package com.subgraph.vega.internal.analysis;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpRequest;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.internal.analysis.urls.UrlExtractor;

public class ContentAnalyzer implements IContentAnalyzer {
	
	private final Logger logger = Logger.getLogger("analysis");
	
	private final ContentAnalyzerFactory factory;
	private final UrlExtractor urlExtractor = new UrlExtractor();
	
	private final Object responseProcessingLock = new Object();
	private List<IResponseProcessingModule> responseProcessingModules;
	private boolean addLinksToModel;
	private boolean defaultAddToRequestLog;
		
	ContentAnalyzer(ContentAnalyzerFactory factory) {
		this.factory = factory;
		this.addLinksToModel = true;
		this.defaultAddToRequestLog = true;
	}

	@Override
	public IContentAnalyzerResult processResponse(IHttpResponse response) {
		return processResponse(response, defaultAddToRequestLog);
	}

	@Override
	public void setDefaultAddToRequestLog(boolean flag) {
		defaultAddToRequestLog = flag;		
	}

	@Override
	public IContentAnalyzerResult processResponse(IHttpResponse response, boolean addToRequestLog) {
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
		
		if(addToRequestLog) 
			workspace.getRequestLog().addRequestResponse(response.getOriginalRequest(), response.getRawResponse(), response.getHost());

		runExtractUrls(result, response, workspace.getWebModel());
		runResponseProcessingModules(response.getOriginalRequest(), response, workspace);
		return result;
	}

	@Override
	public void setResponseProcessingModules(List<IResponseProcessingModule> modules) {
		synchronized(responseProcessingLock) {
			responseProcessingModules = new ArrayList<IResponseProcessingModule>(modules);
		}
	}
	
	private void runExtractUrls(ContentAnalyzerResult result, IHttpResponse response, IWebModel webModel) {
		if(response.isMostlyAscii()) {
			for(URI u : urlExtractor.findUrls(response)) {
				if(addLinksToModel)
					webModel.getWebPathByUri(u);
				result.addUri(u);
			}
		}
	}
	private void runResponseProcessingModules(HttpRequest request, IHttpResponse response, IWorkspace workspace) {
		synchronized(responseProcessingLock) {
			if(responseProcessingModules == null)
				return;
			for(IResponseProcessingModule m: responseProcessingModules)
				m.processResponse(request, response, workspace);
		}
	}

	@Override
	public void setAddLinksToModel(boolean flag) {
		addLinksToModel = flag;
	}
}
