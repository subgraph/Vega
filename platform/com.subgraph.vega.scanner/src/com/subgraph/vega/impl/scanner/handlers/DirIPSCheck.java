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
package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponse.ResponseStatus;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class DirIPSCheck extends CrawlerModule {
	private final static String IPS_TEST =
		"_test1=c:\\windows\\system32\\cmd.exe" +
		"&_test2=/etc/passwd" +
		"&_test3=|/bin/sh" +
		"&_test4=(SELECT * FROM nonexistent) --" +
		"&_test5=>/no/such/file" +
		"&_test6=<script>alert(1)</script>" +
		"&_test7=javascript:alert(1)";

	private final static String IPS_SAFE =
		 "_test1=ccddeeeimmnossstwwxy.:\\\\\\" +
		  "&_test2=acdepsstw//" +
		  "&_test3=bhins//" +
		  "&_test4=CEEFLMORSTeeinnnosttx-*" +
		  "&_test5=cefhilnosu///" +
		  "&_test6=acceiilpprrrssttt1)(" +
		  "&_test7=aaaceijlprrsttv1):(";

	private final InjectionChecks injection = new InjectionChecks();

	@Override
	public void initialize(IPathState ps) {
		final IInjectionModuleContext ctx = ps.createModuleContext();
		ctx.submitRequest(createRequest(ps, IPS_TEST), this, 0);
		ctx.submitRequest(createRequest(ps, IPS_SAFE), this, 1);
	}

	private HttpUriRequest createRequest(IPathState ps, String query) {
		final IHttpRequestEngine requestEngine = ps.getRequestEngine();
		final HttpHost host = ps.getPath().getHttpHost();
		final String requestLine = ps.getPath().getFullPath() + "?" + query;
		return requestEngine.createGetRequest(host, requestLine);
	}

	@Override
	public void runModule(HttpUriRequest request, IHttpResponse response, IInjectionModuleContext ctx) {
		final IPathState ps = ctx.getPathState();
		if(ctx.hasModuleFailed())
			return;

		if(ctx.getCurrentIndex() == 1 && response.isFetchFail()) {
			ctx.error(request, response, "Fetch failed during IPS tests");
			ctx.setModuleFailed();
			injection.initialize(ps);
		}
		ctx.addRequestResponse(request, response);
		ctx.incrementResponseCount();
		if(!ctx.allResponsesReceived())
			return;

		IPathState p404 = ps.get404Parent();
		if(p404 == null || !p404.isIPSDetected()) {
			if(ctx.getSavedResponse(0).getResponseStatus() != IHttpResponse.ResponseStatus.RESPONSE_OK) {
				ctx.debug("Possible IPS filter detected");
				ctx.getPathState().setIPSDetected();

			} else if(!ctx.isFingerprintMatch(0, 1)) {
				ctx.debug("Possible IPS filter detected");
				ctx.getPathState().setIPSDetected();
			}
		} else {
			if(ctx.getSavedResponse(0).getResponseStatus() == ResponseStatus.RESPONSE_OK && ctx.isFingerprintMatch(0, 1)) {
				ctx.debug("Previously detected IPS filter is no longer active");
			}
		}

		injection.initialize(ps);
	}
}
