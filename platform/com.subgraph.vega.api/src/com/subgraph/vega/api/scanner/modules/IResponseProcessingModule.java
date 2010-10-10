package com.subgraph.vega.api.scanner.modules;

import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IResponseProcessingModule {
	boolean responseCodeFilter(int code);
	boolean mimeTypeFilter(String mimeType);
	
	void processResponse(HttpRequest request, IHttpResponse response, IScanModel scanModel);
	
}
