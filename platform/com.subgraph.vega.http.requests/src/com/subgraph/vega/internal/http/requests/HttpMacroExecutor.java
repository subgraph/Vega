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

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.requests.IHttpMacroContext;
import com.subgraph.vega.api.http.requests.IHttpMacroExecutor;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestEngineException;
import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.api.model.macros.IHttpMacroItem;

public class HttpMacroExecutor implements IHttpMacroExecutor {
	private final IHttpRequestEngine requestEngine;
	private final IHttpMacro macro;
	private final Collection<IHttpMacroItem> macroItems;
	private IHttpMacroContext macroContext;
	private Iterator<IHttpMacroItem> macroItemIterator;

	public HttpMacroExecutor(IHttpRequestEngine requestEngine, IHttpMacro macro, IHttpMacroContext macroContext) {
		this.requestEngine = requestEngine;
		this.macro = macro;
		macroItems = macro.getMacroItems();
		this.macroContext = macroContext;
		macroItemIterator = macroItems.iterator();
	}

	@Override
	public IHttpMacro getMacro() {
		return macro;
	}

	@Override
	public IHttpRequestEngine getRequestEngine() {
		return requestEngine;
	}

	@Override
	public IHttpMacroContext getMacroContext() {
		return macroContext;
	}

	@Override
	public boolean hasNext() {
		return macroItemIterator.hasNext();
	}
	
	@Override
	public IHttpResponse sendNextRequest(HttpContext context) throws RequestEngineException, URISyntaxException, UnsupportedEncodingException {
		if (!macroItemIterator.hasNext()) {
			return null;
		}
		IHttpMacroItem macroItem = macroItemIterator.next();
		HttpUriRequest request = macroItem.createRequest(macroContext);
		return requestEngine.sendRequest(request, context);
	}

	@Override
	public IHttpResponse sendNextRequest() throws RequestEngineException, URISyntaxException, UnsupportedEncodingException {
		if (!macroItemIterator.hasNext()) {
			return null;
		}
		IHttpMacroItem macroItem = macroItemIterator.next();
		HttpUriRequest request = macroItem.createRequest(macroContext);
		return requestEngine.sendRequest(request);
	}
	
}
