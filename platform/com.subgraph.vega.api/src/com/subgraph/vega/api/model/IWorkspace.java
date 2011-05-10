package com.subgraph.vega.api.model;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.web.IWebModel;

public interface IWorkspace extends IModelProperties {
	IWebModel getWebModel();
	IScanAlertRepository getScanAlertRepository();
	IRequestLog getRequestLog();
	IHttpConditionManager getHttpConditionMananger();
	IHTMLParser getHTMLParser();
	void consoleWrite(String output);
	void consoleError(String output);
	boolean open();
	void close();
	void lock();
	void unlock();
	void reset();
}
