package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler1 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler2 = new InjectHandler2();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		if(ps.getInjectSkipFlag(0))
			return;
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during directory listing / traversal attack");
			ps.setInjectSkipFlag(0);
			scheduleNext(ps);
			return;
		}
		
		ps.addMiscRequestResponse(data.getFlag(), request, response);
		
		if(ps.incrementMiscCount() < 4)
			return;
		
		if(!ps.isParametric()) {
			if(ps.getMiscResponseCode(0) < 300 && 
					!ps.miscFingerprintMatchesPath(0) && !ps.miscFingerprintsMatch(0, 1)) {
				System.out.println("unique response for /./ for "+ ps.getPath().getFullPath());
				IHttpResponse resp0 = ps.getMiscResponse(0);
				if(resp0 != null)
					ps.responseChecks(ps.createRequest(), resp0);
				
			}
			if(ps.getMiscResponseCode(2) < 300 && !ps.miscFingerprintMatchesPath(2) && !ps.miscFingerprintsMatch(2, 3)) {
				// XXX
				System.out.println("unique response for \\.\\");
				ps.miscResponseChecks(2);				
			}
						
			
		} else  {
			if(!ps.miscFingerprintsMatch(0, 1)) {
				System.out.println("problem: responses for ./val and .../val look different");
				ps.miscResponseChecks(0);
			}
			if(!ps.miscFingerprintsMatch(2, 3)) {
				System.out.println("responses for .\\val and ...\\val look different");
				ps.miscResponseChecks(2);
			}
		}
		
		scheduleNext(ps);
	}
	
	private void scheduleNext(PathState ps) {
		ps.resetMiscData();
		ps.submitMultipleAlteredRequests(injectHandler2, new String[] {"vega>'>\"><vega></vega>", "vega>'>\"></vega><vega>"});
	}

}
