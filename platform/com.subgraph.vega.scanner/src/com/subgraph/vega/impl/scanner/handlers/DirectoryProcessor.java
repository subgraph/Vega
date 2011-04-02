package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class DirectoryProcessor implements ICrawlerResponseProcessor {

	private final Dir404Tests dir404Tests;
	
	public DirectoryProcessor() {
		this.dir404Tests = new Dir404Tests();
	}
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		
		ps.getPath().setVisited(true);

		ps.setResponse(response);
		//System.out.println("DirectoryProcessor: "+ request.getMethod() + " "+ request.getURI());
		// Pivot checks for PIVOT_SERV
		ctx.analyzePage(request, response);
		
		
		dir404Tests.initialize(ps);
		// if parent, secondary_ext_init
		
	}

}
