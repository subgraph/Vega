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

import java.util.Random;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class ParametricCheckHandler extends CrawlerModule {
	private final static Random random = new Random();
	private final ICrawlerResponseProcessor ognlHandler = new OgnlHandler();
	private final InjectionChecks injectionChecks = new InjectionChecks();

	@Override
	public void initialize(IPathState ps) {
		final IInjectionModuleContext ctx = ps.createModuleContext();
		// XXX check URI filter and parameter filter
		if(!ps.isParametric() || false) {
			ctx.debug("not parametric??");
			ps.setDone();
			return;
		}

		final String param = generateBogusParameter();
		for(int i = 0; i < 5; i++) {
			ctx.submitAlteredRequest(this, param, i);
		}

	}
	
	private String generateBogusParameter() {
		final int unique = random.nextInt(9000) + 1000;
		return "asdf" + unique;
	}

	@Override
	public void runModule(HttpUriRequest request, IHttpResponse response,
			IInjectionModuleContext ctx) {

		if(response.isFetchFail()) {
			ctx.error(request, response, "during parameter behavior test");
			maybeScheduleNext(ctx);
			return;
		}


		final IPageFingerprint pathFP = ctx.getPathState().getPathFingerprint();
		if(pathFP != null && pathFP.isSame(response.getPageFingerprint())) {
			ctx.getPathState().setBogusParameter();
			maybeScheduleNext(ctx);
			return;
		}

		if(ctx.getPathState().isBogusParameter()) {
			ctx.debug("We classified parameter as no effect, but now it's changing");
			ctx.getPathState().setResponseVaries();
			// XXX problem varies
			maybeScheduleNext(ctx);
			return;
		}

		if(!ctx.getPathState().has404Fingerprints()) {
			ctx.debug("Adding 404 signature from parameter probe");
			ctx.getPathState().add404Fingerprint(response.getPageFingerprint());
		} else if(!ctx.getPathState().has404FingerprintMatching(response.getPageFingerprint())) {
			ctx.debug("Signature does not match previous responses");
			// XXX problem varies
			ctx.getPathState().setResponseVaries();
		}
		maybeScheduleNext(ctx);
	}

	private void maybeScheduleNext(IInjectionModuleContext ctx) {
		if(ctx.incrementResponseCount() < 5)
			return;
		scheduleNext(ctx.getPathState());
	}

	private void scheduleNext(IPathState ps) {
		final IInjectionModuleContext ctx = ps.createModuleContext();
		final String pname = ps.getFuzzableParameter().getName();
		if(!ps.isBogusParameter() && !ps.getResponseVaries() && pname != null) {
			ctx.submitAlteredParameterNameRequest(ognlHandler, "[0]['"+pname+"']", 0);
			ctx.submitAlteredParameterNameRequest(ognlHandler, "[0]['vega']", 1);
		}
		injectionChecks.initialize(ps);

	}

}
