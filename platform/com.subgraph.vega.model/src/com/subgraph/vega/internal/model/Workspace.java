package com.subgraph.vega.internal.model;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.requestlog.IRequestLog;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.internal.model.scan.ScanModel;

public class Workspace implements IWorkspace {

	private final IWebModel webModel;
	private final IRequestLog requestLog;
	private final IScanModel scanModel;
	
	Workspace(IWebModel model, IRequestLog requestLog, IConsole console, IHTMLParser htmlParser, IScanAlertRepository alertRepository) {
		this.webModel = model;
		this.requestLog = requestLog;
		this.scanModel = new ScanModel(alertRepository, htmlParser, console);
	}
	
	@Override
	public IWebModel getWebModel() {
		return webModel;
	}
	
	@Override
	public IRequestLog getRequestLog() {
		return requestLog;
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IScanModel getScanModel() {
		return scanModel;
	}

}
