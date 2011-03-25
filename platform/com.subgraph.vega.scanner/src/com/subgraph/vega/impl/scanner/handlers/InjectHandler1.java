package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class InjectHandler1 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler2 = new InjectHandler2();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		
		if(ctx.hasModuleFailed())
			return;
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "during directory listing / traversal attack");
			ctx.setModuleFailed();
			scheduleNext(ctx.getPathState());
			return;
		}
		
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		if(ctx.incrementResponseCount() < 4)
			return;
		
		final IPageFingerprint pathFP = ctx.getPathState().getPathFingerprint();
		if(!ctx.getPathState().isParametric()) {
			if(ctx.getSavedResponse(0).getResponseCode() < 300 && !ctx.isFingerprintMatch(0, pathFP) && 
					!ctx.isFingerprintMatch(0, 1)) {
				System.out.println("unique response for /./ for "+ ctx.getPathState().getPath().getFullPath());
				IHttpResponse resp0 = ctx.getSavedResponse(0);
				if(resp0 != null)
					ctx.responseChecks(ctx.getPathState().createRequest(), resp0);
				
			}
			if(ctx.getSavedResponse(2).getResponseCode() < 300 && !ctx.isFingerprintMatch(2, pathFP) && !ctx.isFingerprintMatch(2, 3)) { 
			
				// XXX
				System.out.println("unique response for \\.\\");
				ctx.responseChecks(2);				
			}
						
			
		} else  {
			if(!ctx.isFingerprintMatch(0, 1)) {
				System.out.println("problem: responses for ./val and .../val look different");
				ctx.responseChecks(0);
			}
			if(!ctx.isFingerprintMatch(2, 3)) {
				System.out.println("responses for .\\val and ...\\val look different");
				ctx.responseChecks(2);
			}
		}
		
		scheduleNext(ctx.getPathState());
	}
	
	private void scheduleNext(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		ctx.submitMultipleAlteredRequests(injectHandler2, new String[] {"vega>'>\"><vega></vega>", "vega>'>\"></vega><vega>"});
	}

}
