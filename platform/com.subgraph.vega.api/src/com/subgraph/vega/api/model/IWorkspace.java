package com.subgraph.vega.api.model;

import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.requestlog.IRequestLog;
import com.subgraph.vega.api.scanner.model.IScanModel;

public interface IWorkspace {
	IWebModel getWebModel();
	IScanModel getScanModel();
	IRequestLog getRequestLog();
	void close();
}
