package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PageFingerprint;
import com.subgraph.vega.impl.scanner.state.PathState;

public class ParametricCheckHandler implements ICrawlerResponseProcessor {
	private final static String BOGUS_PARAM = "asdf1234";
	private final ICrawlerResponseProcessor ognlHandler = new OgnlHandler();
	private final InjectionChecks injectionChecks = new InjectionChecks();
	
	public void init(PathState ps) {
		ps.debug("parametric checks on "+ ps.createRequest().getURI());
		// XXX check URI filter and parameter filter
		if(!ps.isParametric() || false) {
			ps.debug("not parametric??");
			ps.setDone();
			return;
		}
		ps.resetMiscData();
		for(int i = 0; i < 15; i++) {
			ps.submitAlteredRequest(this, BOGUS_PARAM, i);
		}
		
	}

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during parameter behavior test");
			scheduleNext(ps);
			return;
		}
		
		final PageFingerprint fp = PageFingerprint.generateFromCodeAndString(response.getResponseCode(), response.getBodyAsString());
		if(ps.matchesPathFingerprint(fp)) {
			ps.debug("Parameter has no effect");
			ps.setBogusParameter();
			scheduleNext(ps);
			return;
		}
		
		if(ps.isBogusParameter()) {
			ps.debug("We classified parameter as no effect, but now it's changing");
			ps.setResponseVaries();
			// XXX problem varies
			scheduleNext(ps);
			return;
		}
		
		if(!ps.has404Fingerprints()) {
			ps.debug("Adding 404 signature from parameter probe");
			ps.add404Fingerprint(fp);
		} else if(!ps.has404FingerprintMatching(fp)) {
			ps.debug("Signature does not match previous responses");
			// XXX problem varies
			ps.setResponseVaries();
		}
		scheduleNext(ps);
	}
	
	private void scheduleNext(PathState ps) {
		if(ps.incrementMiscCount() < 15)
			return;
		final String pname = ps.getFuzzableParameter().getName();
		if(!ps.isBogusParameter() && !ps.getResponseVaries() && pname != null) {
			ps.submitAlteredParameterNameRequest(ognlHandler, "[0]['"+pname+"']", 0);
			ps.submitAlteredParameterNameRequest(ognlHandler, "[0]['vega']", 1);
		}
		injectionChecks.initialize2(ps);
		
	}

}
