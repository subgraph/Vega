package com.subgraph.vega.impl.scanner;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public class ScannerResponseProcessor implements IHttpResponseProcessor {
	private final List<IResponseProcessingModule> responseProcessingModules;
	private final IWorkspace workspace;
	
	public ScannerResponseProcessor(List<IResponseProcessingModule> responseProcessingModules, IWorkspace workspace) {
		this.responseProcessingModules = responseProcessingModules;
		this.workspace = workspace;
	}
	
	@Override
	public void processResponse(HttpRequest request, IHttpResponse response, HttpContext context) {
		if(responseProcessingModules.isEmpty())
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
