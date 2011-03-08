package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PageFingerprint;
import com.subgraph.vega.impl.scanner.state.PathState;

public class OgnlHandler implements ICrawlerResponseProcessor {

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during OGNL tests");
			return;
		}
		
		final PageFingerprint fp = PageFingerprint.generateFromCodeAndString(response.getResponseCode(), response.getBodyAsString());
		if(data.getFlag() == 0 && ps.matchesPathFingerprint(fp))
			ps.incrementOgnlCount();
		if(data.getFlag() == 1 && !ps.matchesPathFingerprint(fp))
			ps.incrementOgnlCount();
		
		if(ps.getOgnlCount() == 2) {
			System.out.println("Problem: response to [0]['name']=... identical to name=...");
		}
	}

}
