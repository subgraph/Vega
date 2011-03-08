package com.subgraph.vega.api.scanner.modules;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebHost;

public interface IPerHostScannerModule extends IScannerModule {
	void runScan(IWebHost host, IHttpRequestEngine requestEngine, IWorkspace workspace);
}
