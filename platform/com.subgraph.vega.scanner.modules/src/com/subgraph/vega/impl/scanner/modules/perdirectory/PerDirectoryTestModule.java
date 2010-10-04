package com.subgraph.vega.impl.scanner.modules.perdirectory;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;

public class PerDirectoryTestModule implements IPerDirectoryScannerModule {

	@Override
	public void runScan(IScanDirectory directory, IHttpRequestEngine requestEngine,
			IScanModel scanModel) {
		System.out.println("PerDirectoryTestModule on directory: "+ directory.getURI());
		IScanAlert alert = scanModel.createAlert("test");
		alert.setProperty("resource", directory.getURI().getPath());
		scanModel.addAlert(alert);
	}

}
