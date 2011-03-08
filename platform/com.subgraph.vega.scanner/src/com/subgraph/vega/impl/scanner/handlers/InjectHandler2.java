package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler2 implements ICrawlerResponseProcessor {
	private final ICrawlerResponseProcessor injectHandler3 = new InjectHandler3();

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(ps.getInjectSkipFlag(1))
			return;
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during backend xml injection attacks");
			ps.setInjectSkipFlag(1);
			scheduleNext(ps);
			return;
		}
		ps.addMiscRequestResponse(data.getFlag(), request, response);
		
		if(ps.incrementMiscCount() < 2)
			return;
		
		if(!ps.miscFingerprintsMatch(0, 1)) {
			// XXX
			System.out.println("responses for <vega></vega> and </vega><vega> look different for "+ ps.getPath().getFullPath());
			ps.miscResponseChecks(1);
		}
		
		scheduleNext(ps);
		
	}
	
	private void scheduleNext(PathState ps) {
		ps.resetMiscData();
		final String[] injectme = {
				"`true`", "`false`", "`uname`", 
				"\"`true`\"", "\"`false`\"", "\"`uname`\"",
				"'true'", "'false'", "'uname'" 
		};
		
		ps.submitMultipleAlteredRequests(injectHandler3, injectme, true);
		
	}

}
