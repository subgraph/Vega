package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler3 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler4 = new InjectHandler4();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(ps.getInjectSkipFlag(2))
			return;
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during path based shell injection attacks");
			ps.setInjectSkipFlag(2);
			scheduleNext(ps);
			return;
		}
		
		ps.addMiscRequestResponse(data.getFlag(), request, response);
		
		if(ps.incrementMiscCount() < 9)
			return;
		if(ps.miscFingerprintsMatch(0, 1) && !ps.miscFingerprintsMatch(0, 2)) {
			// XXX
			System.out.println("1 responses to `true` and `false` are different than `uname` for "+ ps.getPath().getFullPath());
			ps.miscResponseChecks(2);

		}
		if(ps.miscFingerprintsMatch(3, 4) && !ps.miscFingerprintsMatch(3, 5)) {
			// XXX
			System.out.println("2 responses to `true` and `false` are different than `uname` for "+ ps.getPath().getFullPath());
			ps.miscResponseChecks(5);
			
		}
		if(ps.miscFingerprintsMatch(6, 7) && !ps.miscFingerprintsMatch(6, 8)) {
			// XXX
			System.out.println("3 responses to `true` and `false` are different than `uname` for "+ ps.getPath().getFullPath());
			

			
			ps.miscResponseChecks(8);
		}
		scheduleNext(ps);
	}
	
	private void scheduleNext(PathState ps) {
		
		ps.resetMiscData();
		final int xid1 = ps.allocateXssId();
		final int xid2 = ps.allocateXssId();
		final String tag1 = ps.createXssTag(xid1);
		final String tag2 = ps.createXssTag(".htaccess.aspx", xid2);
		
		
		HttpUriRequest req1 = ps.createAlteredRequest(tag1, true);
		if(req1 != null) {
			ps.registerXssRequest(req1, xid1);
			req1.addHeader("Referer", tag1);
			ps.submitRequest(req1, injectHandler4, 0);
		}
		
		final HttpUriRequest req2 = ps.createAlteredRequest(tag2, false);
		if(req2 != null) {
			ps.registerXssRequest(req2, xid2);
			ps.submitRequest(req2, injectHandler4, 1);
		}
		
	}

}
