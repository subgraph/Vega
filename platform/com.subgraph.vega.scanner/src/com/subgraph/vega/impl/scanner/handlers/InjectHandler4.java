package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectHandler4 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler5 = new InjectHandler5();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		ps.contentChecks(request, response);
		
		if(data.getFlag() > 0)
			return;
		
		ps.resetMiscData();
		String injectme[] = { "http://vega.invalid/;?", "//vega.invalid/;?",  "vega://invalid/;?"};
		ps.submitMultipleAlteredRequests(injectHandler5, injectme);
	}

}
