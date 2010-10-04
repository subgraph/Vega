package com.subgraph.vega.impl.scanner.modules.perhost;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanHost;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;

public class PerHostTestModule implements IPerHostScannerModule {

	@Override
	public void runScan(IScanHost host, IHttpRequestEngine requestEngine,
			IScanModel scanModel) {
		System.out.println("PerHostTestModule on host: "+ host.getURI());
		IScanAlert alert = scanModel.createAlert("test");
		scanModel.addAlert(alert);
	}

}
