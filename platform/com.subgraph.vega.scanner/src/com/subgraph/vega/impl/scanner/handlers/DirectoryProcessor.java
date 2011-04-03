package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class DirectoryProcessor implements ICrawlerResponseProcessor {

	private final Dir404Tests dir404Tests;
	private final SecondaryExtChecks secondaryExt;
	
	public DirectoryProcessor() {
		this.dir404Tests = new Dir404Tests();
		this.secondaryExt = new SecondaryExtChecks();
	}
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		
		ps.getPath().setVisited(true);

		ps.setResponse(response);
		
		if(ps.isRootPath())
			ctx.pivotChecks(request, response);
		
		dir404Tests.initialize(ps);
		
		if(ps.get404Parent() != null) 
			secondaryExt.initialize(ps);
	}

}
