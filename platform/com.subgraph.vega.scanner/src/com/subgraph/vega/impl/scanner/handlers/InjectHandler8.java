package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler8 implements ICrawlerResponseProcessor {
	private final ICrawlerResponseProcessor injectHandler9 = new InjectHandler9();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(ps.getInjectSkipFlag(4))
			return;
		if(response.isFetchFail()) {
			ps.error(request, response, "during format string attacks");
			ps.setInjectSkipAdd(4);
			scheduleNext(ps);
			return;
		}
		ps.addMiscRequestResponse(data.getFlag(), request, response);
		if(ps.incrementMiscCount() < 2)
			return;
		
		if(!ps.miscFingerprintsMatch(0, 1)) {
			System.out.println("response to %dn%dn%dn... different than to %nd%nd%nd...");
			ps.miscResponseChecks(1);
		}
		scheduleNext(ps);
	}

	private void scheduleNext(PathState ps) {
		ps.resetMiscData();
		
		final String[] injectme = { "-0000012345", "-2147483649", "-2147483648", 
				"0000012345", "2147483647", "2147483648", "4294967295",  "4294967296", "0000023456"};
		ps.submitMultipleAlteredRequests(injectHandler9, injectme);
	}
}
