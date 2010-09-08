package com.subgraph.vega.internal.model;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.requestlog.IRequestLog;



public class Model implements IModel {
		
	private IWebModel webModel;
	private IRequestLog requestLog;
	
	private IWorkspace workspace;
	
	@Override
	public boolean openWorkspace(String path) {
		return false;
	}

	@Override
	public IWorkspace getCurrentWorkspace() {
		if(workspace == null)
			workspace = new Workspace(webModel, requestLog);
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
}
