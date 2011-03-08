package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler7 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler8 = new InjectHandler8();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(ps.getInjectSkipFlag(3))
			return;
		if(response.isFetchFail()) {
			ps.error(request, response, "during SQL injection attacks");
			ps.setInjectSkipFlag(3);
			scheduleNext(ps);
			return;
		}
		
		ps.addMiscRequestResponse(data.getFlag(), request, response);
		if(ps.incrementMiscCount() < 8)
			return;
		
		if(ps.miscFingerprintsMatch(0, 1) && !ps.miscFingerprintsMatch(0, 2)) {
			System.out.println("response suggests arithmetic evaluation on server side (type 1)");
			ps.miscResponseChecks(0);
			ps.miscResponseChecks(2);
			
		}
		
		if(ps.miscFingerprintsMatch(1, 6) && !ps.miscFingerprintsMatch(6, 7)) {
			System.out.println("response suggests arithmetic evaluation on server side (type 2)");
			ps.miscResponseChecks(6);
			ps.miscResponseChecks(7);
		}
		
		if(!ps.miscFingerprintsMatch(3, 4) && !ps.miscFingerprintsMatch(3, 5)) {
			System.out.println("response to '\" different than to \\'\\\"");
			ps.miscResponseChecks(3);
			ps.miscResponseChecks(4);
		}
		
		scheduleNext(ps);
	}
	
	private void scheduleNext(PathState ps) {
		ps.resetMiscData();
		ps.submitMultipleAlteredRequests(injectHandler8, new String[] { "vega%dn%dn%dn%dn%dn%dn%dn%dn", "vega%nd%nd%nd%nd%nd%nd%nd%nd"});
	}

}
