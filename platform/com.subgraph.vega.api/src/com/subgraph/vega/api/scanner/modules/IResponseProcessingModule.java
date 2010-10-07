package com.subgraph.vega.api.scanner.modules;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IResponseProcessingModule {
	boolean responseCodeFilter(int code);
	boolean mimeTypeFilter(String mimeType);
	
	void processResponse(HttpRequest request, HttpResponse response, IScanModel scanModel);
	
}
