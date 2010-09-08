package com.subgraph.vega.internal.model;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.requestlog.IRequestLog;

public class Workspace implements IWorkspace {

	private final IWebModel webModel;
	private final IRequestLog requestLog;
	
	Workspace(IWebModel model, IRequestLog requestLog) {
		this.webModel = model;
		this.requestLog = requestLog;
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

}
