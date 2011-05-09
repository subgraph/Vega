package com.subgraph.vega.api.scanner.modules;

import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;

public interface IResponseProcessingModule extends IScannerModule {
	boolean responseCodeFilter(int code);
	boolean mimeTypeFilter(String mimeType);
	
	void processResponse(long scanId, HttpRequest request, IHttpResponse response, IWorkspace workspace);

	
}
