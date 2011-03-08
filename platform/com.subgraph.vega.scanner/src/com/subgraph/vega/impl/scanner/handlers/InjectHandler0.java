package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PageFingerprint;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler0 implements ICrawlerResponseProcessor {

	private final InjectionChecks injectionChecks;
	private final ICrawlerResponseProcessor injectHandler1;
	
	public InjectHandler0(InjectionChecks injectionChecks) {
		this.injectionChecks = injectionChecks;
		this.injectHandler1 = new InjectHandler1();
	}
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during page variablility checks");
		} else {
			final PageFingerprint fp = PageFingerprint.generateFromCodeAndString(response.getResponseCode(), response.getBodyAsString());
			if(!fp.isSame(ps.getPathFingerprint())) {
				ps.setResponseVaries();
				ps.debug("Response varies "+ request.getURI() + " node "+ ps);
				ps.debug(fp + " != "+ ps.getPathFingerprint());
				// XXX problem response varies
			} else {
				//System.out.println("fp match for "+ request.getURI());
			}
		}
		
		if(ps.incrementMiscCount() < 15)
			return;
		
		if(ps.getResponseVaries()) {
			injectionChecks.endChecks();
			return;
		}
		
		ps.resetMiscData();
		
		ps.submitMultipleAlteredRequests(injectHandler1, createInjectables(ps));
		
	}
	
	private String[] createInjectables(PathState ps) {
		if(!ps.isParametric()) {
			return new String[] { "/./", "/.vega/", "\\.\\", "\\.vega\\" };	
		}
		
		final NameValuePair fuzzable = ps.getFuzzableParameter();
		ps.setInjectSkipAdd(6);
		final String[] injectables = { ".../", "./", "...\\", ".\\" };
		final String[] ret = new String[injectables.length];
		for(int i = 0; i < injectables.length; i++)
			ret[i] = injectables[i] + fuzzable.getValue();
		return ret;	
	}

}
