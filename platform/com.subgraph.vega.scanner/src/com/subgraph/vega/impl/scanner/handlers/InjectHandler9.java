package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler9 implements ICrawlerResponseProcessor {

	private final EndInjectionChecks end = new EndInjectionChecks();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(ps.getInjectSkipFlag(5)) 
			return;
		if(response.isFetchFail()) {
			ps.setInjectSkipAdd(5);
			scheduleNext(ps);
			return;
		}
		
		ps.addMiscRequestResponse(data.getFlag(), request, response);
		if(ps.incrementMiscCount() < 9)
			return;
		
		if(!ps.miscFingerprintsMatch(3, 8)) {
			scheduleNext(ps);
			return;
		}
		
		if(!ps.miscFingerprintsMatch(0, 1)) {
			System.out.println("response to -(2^31-1) different than to -12345");
			ps.miscResponseChecks(1);
		}
		
		if(!ps.miscFingerprintsMatch(0, 2)) {
			System.out.println("response to -2^31 different than to -12345");
			ps.miscResponseChecks(2);
		}

		if(!ps.miscFingerprintsMatch(3, 4)) {
			System.out.println("response to 2^31-1 different than to 12345");
			ps.miscResponseChecks(4);
		}

		if(!ps.miscFingerprintsMatch(3, 5)) {
			System.out.println("response to 2^31 different than to 12345");
			ps.miscResponseChecks(5);
		}

		if(!ps.miscFingerprintsMatch(3, 6)) {
			System.out.println("response to 2^32-1 different than to 12345");
			ps.miscResponseChecks(6);
		}

		if(!ps.miscFingerprintsMatch(3, 7)) {
			System.out.println("response to 2^32 different than to 12345");
			ps.miscResponseChecks(7);
		}

		scheduleNext(ps);
	}
	
	private void scheduleNext(PathState ps) {
		ps.resetMiscData();
		end.endChecks(ps);
	}

}
