/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.api.model;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.tags.ITagModel;
import com.subgraph.vega.api.model.web.IWebModel;

public interface IWorkspace extends IModelProperties {
	ITagModel getTagModel();
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
