package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class DirectoryProcessor implements ICrawlerResponseProcessor {
	private static final String PAGE_DOES_NOT_EXIST = "/nosuchpage123";
	private final Dir404Processor dir404Processor;
	
	public DirectoryProcessor() {
		this.dir404Processor = new Dir404Processor();
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
		
		final IModuleContext newCtx = ps.createModuleContext();
		

		newCtx.submitAlteredRequest(dir404Processor, PAGE_DOES_NOT_EXIST);
		// if parent, secondary_ext_init
		
	}

}
