package com.subgraph.vega.api.scanner.modules;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.model.IScanHost;
import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IPerHostScannerModule {
	void runScan(IScanHost host, IHttpRequestEngine requestEngine, IScanModel scanModel);

}
