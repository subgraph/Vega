package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class InjectHandler0 implements ICrawlerResponseProcessor {

	private final InjectionChecks injectionChecks;
	private final ICrawlerResponseProcessor injectHandler1;
	
	public InjectHandler0(InjectionChecks injectionChecks) {
		this.injectionChecks = injectionChecks;
		this.injectHandler1 = new InjectHandler1();
	}
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		
		final IModuleContext ctx = (IModuleContext) argument;
		final IPathState ps = ctx.getPathState();		
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "during page variablility checks");
		} else {
			final IPageFingerprint fp = response.getPageFingerprint();
			if(fp == null || !fp.isSame(ps.getPathFingerprint())) {
				ctx.getPathState().setResponseVaries();
				
				ctx.debug("Response varies "+ request.getURI() + " node "+ ctx.getPathState());
				ctx.debug(fp + " != "+ ps.getPathFingerprint());
				// XXX problem response varies
			} else {
				//System.out.println("fp match for "+ request.getURI());
			}
		}
		
		if(ctx.incrementResponseCount() < 15)
			return;
		
		if(ps.getResponseVaries()) {
			injectionChecks.endChecks();
			return;
		}
		
		IModuleContext next = ps.createModuleContext();
		next.submitMultipleAlteredRequests(injectHandler1, createInjectables(ps));
		
	}
	
	private String[] createInjectables(IPathState ps) {
		if(!ps.isParametric()) {
			return new String[] { "/./", "/.vega/", "\\.\\", "\\.vega\\" };	
		}
		
		final NameValuePair fuzzable = ps.getFuzzableParameter();
		
		final String[] injectables = { ".../", "./", "...\\", ".\\" };
		final String[] ret = new String[injectables.length];
		for(int i = 0; i < injectables.length; i++)
			ret[i] = injectables[i] + fuzzable.getValue();
		return ret;	
	}

}
