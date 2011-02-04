package com.subgraph.vega.api.scanner.modules;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebMountPoint;

public interface IPerMountPointModule {
	void runModule(IWebMountPoint mountPoint, IHttpRequestEngine requestEngine, IWorkspace workspace);
}
