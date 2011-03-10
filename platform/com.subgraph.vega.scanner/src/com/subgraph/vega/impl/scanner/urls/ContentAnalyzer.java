package com.subgraph.vega.impl.scanner.urls;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.impl.scanner.state.PathState;

public class ContentAnalyzer {
	
	private final List<IResponseProcessingModule> responseProcessingModules;
	private final IWorkspace workspace;
	
	public ContentAnalyzer(List<IResponseProcessingModule> responseProcessingModules, IWorkspace workspace) {
		this.responseProcessingModules = responseProcessingModules;
		this.workspace = workspace;
	}
	public void analyze(HttpUriRequest request, IHttpResponse response, PathState pathState)
	{
		if(responseProcessingModules.isEmpty() || !response.isMostlyAscii())
			return;
		final HttpResponse httpResponse = response.getRawResponse();
		final int statusCode = httpResponse.getStatusLine().getStatusCode();
		final String mimeType = responseToMimeType(httpResponse);
		
		for(IResponseProcessingModule m: responseProcessingModules) {
			if(m.responseCodeFilter(statusCode) && m.mimeTypeFilter(mimeType)) 
				m.processResponse(request, response, workspace);
		}
	}

	private String responseToMimeType(HttpResponse response) {
		final HttpEntity entity = response.getEntity();
		if(entity == null)
			return null;
		final Header contentType = entity.getContentType();
		if(contentType == null)
			return null;
		return contentType.getValue();
	}
}
