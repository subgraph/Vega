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
package com.subgraph.vega.api.scanner;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IModelProperties;

public interface IModuleContext extends IModelProperties {
	void error(HttpUriRequest request, IHttpResponse response, String message);
	void debug(String msg);
	void addStringHighlight(String str);
	void addRegexHighlight(String regex);
	void addRegexCaseInsensitiveHighlight(String regex);
	void reset();
	void publishAlert(String type, String key, String message, HttpRequest request, IHttpResponse response, Object ...properties);
	void publishAlert(String type, String message, HttpRequest request, IHttpResponse response, Object ...properties);
}
