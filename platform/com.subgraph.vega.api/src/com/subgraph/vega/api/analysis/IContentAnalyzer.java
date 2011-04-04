package com.subgraph.vega.api.analysis;

import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public interface IContentAnalyzer {
	IContentAnalyzerResult processResponse(IHttpResponse response);
	IContentAnalyzerResult processResponse(IHttpResponse response, boolean addToRequestLog, boolean scrapePage);
	void setResponseProcessingModules(List<IResponseProcessingModule> modules);
	void setAddLinksToModel(boolean flag);
	void setDefaultAddToRequestLog(boolean flag);
	void resetResponseProcessingQueue();
}
