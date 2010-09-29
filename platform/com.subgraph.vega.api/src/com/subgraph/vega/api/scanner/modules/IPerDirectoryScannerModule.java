package com.subgraph.vega.api.scanner.modules;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IPerDirectoryScannerModule {
	void runScan(IScanDirectory directory, IHttpRequestEngine requestEngine, IScanModel scanModel);
}
