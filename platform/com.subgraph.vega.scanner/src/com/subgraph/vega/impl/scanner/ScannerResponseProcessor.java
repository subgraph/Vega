package com.subgraph.vega.impl.scanner;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public class ScannerResponseProcessor implements IHttpResponseProcessor {
	private final List<IResponseProcessingModule> responseProcessingModules;
	private final IScanModel scanModel;
	
	public ScannerResponseProcessor(List<IResponseProcessingModule> responseProcessingModules, IScanModel scanModel) {
		this.responseProcessingModules = responseProcessingModules;
		this.scanModel = scanModel;
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
				m.processResponse(request, response, scanModel);
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
