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
import com.subgraph.vega.api.model.identity.IIdentityModel;
import com.subgraph.vega.api.model.macros.IHttpMacroModel;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.scope.ITargetScopeManager;
import com.subgraph.vega.api.model.tags.ITagModel;
import com.subgraph.vega.api.model.variables.IVariableModel;
import com.subgraph.vega.api.model.web.IWebModel;

public interface IWorkspace extends IModelProperties {
	IModel getModel();
	IModelVersion getModelVersion();
	ITagModel getTagModel();
	IWebModel getWebModel();
	IVariableModel getVariableModel();
	IHttpMacroModel getHttpMacroModel();
	IIdentityModel getIdentityModel();
	IScanAlertRepository getScanAlertRepository();
	IRequestLog getRequestLog();
	IHttpConditionManager getHttpConditionMananger();
	IHTMLParser getHTMLParser();
	ITargetScopeManager getTargetScopeManager();
	void consoleWrite(String output);
	void consoleDebug(String output);
	void consoleError(String output);
	boolean open();
	void close();
	void lock();
	void unlock();
	void reset();
}
