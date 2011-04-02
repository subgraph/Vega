package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class PutChecks extends CrawlerModule {

	private final InjectionChecks injectionChecks;
	
	PutChecks(InjectionChecks injectionChecks) {
		this.injectionChecks = injectionChecks;
	}
	
	@Override
	public void initialize(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		HttpUriRequest req = new HttpPut(ps.getPath().getUri().resolve("PUT-putfile"));
		ctx.submitRequest(req, this);
	}

	@Override
	public void runModule(HttpUriRequest request, IHttpResponse response,
			IModuleContext ctx) {
		final IPathState ps = ctx.getPathState();
		if(response.isFetchFail()) {
			ctx.error(request, response, "during PUT checks");
		} else {
			final int rc = response.getResponseCode();
			final IPageFingerprint fp = response.getPageFingerprint();
			
			if(rc >= 200 && rc < 300 && !ps.matchesPathFingerprint(fp)) {
				ctx.publishAlert("vinfo-http-put", "HTTP PUT succeeded", request, response);
			}
		}
		injectionChecks.runPageVariabilityCheck(ctx.getPathState());		
	}
}
