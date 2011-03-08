package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PageFingerprint;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectPutHandler implements ICrawlerResponseProcessor {

	private final InjectionChecks injectionChecks;
	
	InjectPutHandler(InjectionChecks injectionChecks) {
		this.injectionChecks = injectionChecks;
	}
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		if(response.isFetchFail()) {
			ps.error(request, response, "during PUT checks");
		} else {
			final int rc = response.getResponseCode();
			final PageFingerprint fp = PageFingerprint.generateFromCodeAndString(rc, response.getBodyAsString());
			
			if(rc >= 200 && rc < 300 && !fp.isSame(ps.getPathFingerprint())) {
				System.out.println("PUT directory");
			}
		}
		injectionChecks.initialize2(ps);
	}

}
