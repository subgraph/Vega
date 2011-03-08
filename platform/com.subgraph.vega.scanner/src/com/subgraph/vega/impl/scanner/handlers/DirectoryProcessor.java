package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PathState;

public class DirectoryProcessor implements ICrawlerResponseProcessor {
	private static final String PAGE_DOES_NOT_EXIST = "/nosuchpage123";
	private final Dir404Processor dir404Processor;
	
	public DirectoryProcessor() {
		this.dir404Processor = new Dir404Processor();
	}
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		
		ps.getPath().setVisited(true);

		ps.setResponse(response);
		//System.out.println("DirectoryProcessor: "+ request.getMethod() + " "+ request.getURI());
		// Pivot checks for PIVOT_SERV
		ps.analyzePage(request, response);
		ps.resetMiscData();

		ps.submitAlteredRequest(dir404Processor, PAGE_DOES_NOT_EXIST);
		// if parent, secondary_ext_init
		
	}

}
