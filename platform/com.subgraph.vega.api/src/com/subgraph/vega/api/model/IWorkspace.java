package com.subgraph.vega.api.model;

import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.requestlog.IRequestLog;

public interface IWorkspace {
	IWebModel getWebModel();
	IRequestLog getRequestLog();
	void close();
}
