package com.subgraph.vega.api.scanner.modules;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebPath;

public interface IPerDirectoryScannerModule extends IScannerModule {
	void runScan(IWebPath directory, IHttpRequestEngine requestEngine, IWorkspace workspace);

}
