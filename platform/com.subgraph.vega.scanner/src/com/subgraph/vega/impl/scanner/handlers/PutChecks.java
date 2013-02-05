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

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicRequestLine;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.http.requests.custom.VegaHttpUriRequest;

public class PutChecks extends CrawlerModule {

	private final InjectionChecks injectionChecks;

	PutChecks(InjectionChecks injectionChecks) {
		this.injectionChecks = injectionChecks;
	}

	@Override
	public void initialize(IPathState ps) {
		final IInjectionModuleContext ctx = ps.createModuleContext();
		URI u = ps.getPath().getUri().resolve("PUT-putfile");
		HttpHost host = URIUtils.extractHost(u);
		RequestLine line = new BasicRequestLine(HttpPut.METHOD_NAME, u.getPath(), HttpVersion.HTTP_1_1);
		VegaHttpUriRequest r = new VegaHttpUriRequest(host, line);
		ctx.submitRequest(r, this);
	}

	@Override
	public void runModule(HttpUriRequest request, IHttpResponse response,
			IInjectionModuleContext ctx) {
		final IPathState ps = ctx.getPathState();
		if(response.isFetchFail()) {
			ctx.error(request, response, "during PUT checks");
		} else {
			final int rc = response.getResponseCode();
			final IPageFingerprint fp = response.getPageFingerprint();
			
			if(rc >= 200 && rc < 300 && !ps.matchesPathFingerprint(fp) && !ps.has404FingerprintMatching(fp)) {
				final String resource = request.getURI().toString();
				final String key = "vinfo-http-put:" + resource;
				ctx.publishAlert("vinfo-http-put", key, "HTTP PUT succeeded", request, response, "resource", resource);
			}
		}

		injectionChecks.runPageVariabilityCheck(ctx.getPathState());
	}
}
