package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PageFingerprint;
import com.subgraph.vega.impl.scanner.state.PathState;

public class Dir404Processor implements ICrawlerResponseProcessor {
	private static final String PAGE_DOES_NOT_EXIST = "/nosuchpage123";

	//private final DirParentCallback dirParentCallback = new DirParentCallback();
	private final InjectionChecks injection = new InjectionChecks();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(ps.getSkip404()) {
			scheduleNext(ps, data.getFlag() == 0);
			return;
		}
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during 404 response checks");
			scheduleNext(ps, data.getFlag() == 0);
			return;
		}
		
		
		final PageFingerprint fp = PageFingerprint.generateFromCodeAndString(response.getResponseCode(), response.getBodyAsString());
		
		
		if(data.getFlag() == 0 && !ps.isSureDirectory() && !ps.isRootPath() && (ps.getResponse() != null)) {
			if(ps.getParentState().matchesPathFingerprint(fp)) {
				ps.debug("First 404 probe identical to parent page");
				scheduleNext(ps, data.getFlag() == 0);
				return;
			}
		} else if (data.getFlag() == 0) {
			ps.debug("First 404 probe differs from parent");
		}
		
		if(!ps.add404Fingerprint(fp)) {
			System.out.println("problem too many 404 signatures found");
			ps.setSkip404();
			scheduleNext(ps, data.getFlag() == 0);
			return;
		}
		
		
		scheduleNext(ps, data.getFlag() == 0);
	}

	public void scheduleNext(PathState ps, boolean first) {
		if(first) {
			if(!ps.has404Fingerprints()) {
				ps.debug("First probe failed to yield a signature");
			
			} else {
			
				ps.pivotChecks(ps.createRequest(), ps.getResponse());

				// 	XXX check case
				// XXX add extension probes
			
				if(!ps.isParametric()) {
					ps.submitAlteredRequest(this, "lpt9", 1);
					ps.submitAlteredRequest(this, "~"+PAGE_DOES_NOT_EXIST, 1);
					ps.submitAlteredRequest(this, PAGE_DOES_NOT_EXIST, 1);
				}
			}
		}
		
		if(ps.incrementMiscCount() < 4)
			return;
		
		
		if(!ps.has404Fingerprints() || ps.getSkip404() ) {
			final int code = (ps.getResponse() == null) ? (0) : (ps.getResponse().getResponseCode());
			ps.debug("404 detection failed");
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
				PageFingerprint pageFP = ps.getPathFingerprint();
				PageFingerprint parentFP = ps.getParentState().getPathFingerprint();
				if(pageFP != null && !pageFP.isSame(parentFP)) {
					if(!ps.hasParent404Fingerprint(pageFP)) {
						ps.pivotChecks(ps.createRequest(), ps.getResponse());
					}
				}
			}
			
		} else {
			//ps.debug("404 detection successful");
		}
		// XXX dir parent tests
		//ps.submitRequest(dirParentCallback);
		
		// XXX do replace second last segment test
		ps.resetMiscData();
		// XXX move unlock child if you move injection init
		ps.unlockChildren();
		injection.intitialize(ps);
	}
}
