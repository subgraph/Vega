package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class Dir404Processor implements ICrawlerResponseProcessor {
	private static final String PAGE_DOES_NOT_EXIST = "nosuchpage123";

	//private final DirParentCallback dirParentCallback = new DirParentCallback();
	private final InjectionChecks injection = new InjectionChecks();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		if(ctx.hasModuleFailed()) {
			scheduleNext(ctx, ps, ctx.getCurrentIndex() == 0);
			return;
		}
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "during 404 response checks");
			scheduleNext(ctx, ps, ctx.getCurrentIndex() == 0);
			return;
		}
		
		
		final IPageFingerprint fp = response.getPageFingerprint();
		
		
		
		if(ctx.getCurrentIndex() == 0 && !ps.isSureDirectory() && !ps.isRootPath() && (ps.getResponse() != null)) {
			if(ps.getParentState().matchesPathFingerprint(fp)) {
				ctx.debug("First 404 probe identical to parent page");
				scheduleNext(ctx, ps, ctx.getCurrentIndex() == 0);
				return;
			}
		} else if (ctx.getCurrentIndex() == 0) {
			ctx.debug("First 404 probe differs from parent");
		}
		
		if(!ps.add404Fingerprint(fp)) {
			System.out.println("problem too many 404 signatures found");
			ctx.setModuleFailed();
			scheduleNext(ctx, ps, ctx.getCurrentIndex() == 0);
			return;
		}
		
		scheduleNext(ctx, ps, ctx.getCurrentIndex() == 0);
	}

	public void scheduleNext(IModuleContext ctx, IPathState ps, boolean first) {
		if(first) {
			if(!ps.has404Fingerprints()) {
				ctx.debug("First probe failed to yield a signature");
			
			} else {
			
				//ps.pivotChecks(ps.createRequest(), ps.getResponse());

				// 	XXX check case
				// XXX add extension probes
			
				if(!ps.isParametric()) {
					ctx.submitAlteredRequest(this, "lpt9", 1);
					ctx.submitAlteredRequest(this, "~"+PAGE_DOES_NOT_EXIST, 1);
					ctx.submitAlteredRequest(this, PAGE_DOES_NOT_EXIST, 1);
				}
			}
		}
		int nn = ctx.incrementResponseCount();
		
		if(nn < 4)
			return;
		
		if(!ps.has404Fingerprints() || ctx.hasModuleFailed()) {
			final int code = (ps.getResponse() == null) ? (0) : (ps.getResponse().getResponseCode());
			ctx.debug("404 detection failed");
			if(code == 404) {
				ps.setPageMissing();
			} else if (code > 400) {
				// XXX prob no access
				if(code == 401) {
					// XXX prob auth required
				} else if(code >= 500) {
					// XXX prob serv error
				}
			} else {
				if(ps.getParentState() != null)
				// XXX PIVOT_PATHINFO
					;
				else
					; // prob no distintive 404
			}
			ps.clear404Fingerprints();
			if(ps.getParentState() == null) {
				// XXX PIVOT CHECKS
			} else {
				IPageFingerprint pageFP = ps.getPathFingerprint();
				IPageFingerprint parentFP = ps.getParentState().getPathFingerprint();
				if(pageFP != null && !pageFP.isSame(parentFP)) {
					if(!ps.hasParent404Fingerprint(pageFP)) {
						ctx.pivotChecks(ps.createRequest(), ps.getResponse());
					}
				}
			}
			
		} else {
			//ps.debug("404 detection successful");
		}
		// XXX dir parent tests
		//ps.submitRequest(dirParentCallback);
		
		// XXX do replace second last segment test
		// XXX move unlock child if you move injection init
		ps.unlockChildren();
		injection.intitialize(ps);
	}
}
