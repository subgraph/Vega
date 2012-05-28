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
package com.subgraph.vega.internal.http.requests;

import java.util.Map;

import com.subgraph.vega.api.http.requests.IHttpMacroContext;

public class HttpMacroContext implements IHttpMacroContext {
	private Map<String, String> dict;

	public HttpMacroContext() {
	}
	
	@Override
	public void setDict(Map<String, String> dict) {
		this.dict = dict;
	}

	@Override
	public Map<String, String> getDict() {
		return dict;
	}

}
