package com.subgraph.vega.internal.model;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.requestlog.IRequestLog;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;

public class Model implements IModel {
		
	private IWebModel webModel;
	private IRequestLog requestLog;
	private IConsole console;
	private IHTMLParser htmlParser;
	private IScanAlertRepository alertRepository;
	
	private IWorkspace workspace;
	
	@Override
	public boolean openWorkspace(String path) {
		return false;
	}

	@Override
	public IWorkspace getCurrentWorkspace() {
		if(workspace == null)
			workspace = new Workspace(webModel, requestLog, console, htmlParser, alertRepository);
		return workspace;
	}
	
	protected void setWebModel(IWebModel webModel) {
		this.webModel = webModel;
	}
	
	protected void unsetWebModel(IWebModel webModel) {
		this.webModel = null;
	}
	
	protected void setRequestLog(IRequestLog requestLog) {
		this.requestLog = requestLog;
	}
	
	protected void unsetRequestLog(IRequestLog requestLog) {
		this.requestLog = null;
	}
	
	protected void setConsole(IConsole console) {
		this.console = console;	
	}

	protected void unsetConsole(IConsole console) {
		this.console = null;
	}
	
	protected void setHTMLParser(IHTMLParser parser) {
		this.htmlParser = parser;
	}
	
	protected void unsetHTMLParser(IHTMLParser parser) {
		this.htmlParser = null;
	}
	
	protected void setAlertRepository(IScanAlertRepository alertRepository) {
		this.alertRepository = alertRepository;
	}
	protected void unsetAlertRepository(IScanAlertRepository alertRepository) {
		this.alertRepository = null;
	}
}

