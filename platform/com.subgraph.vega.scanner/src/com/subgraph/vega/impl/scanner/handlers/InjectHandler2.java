package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class InjectHandler2 implements ICrawlerResponseProcessor {
	private final ICrawlerResponseProcessor injectHandler3 = new InjectHandler3();

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;

		if(ctx.hasModuleFailed())
			return;
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "during backend xml injection attacks");
			ctx.setModuleFailed();
			scheduleNext(ctx.getPathState());
			return;
		}
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		if(ctx.incrementResponseCount() < 2)
			return;
		
		if(!ctx.isFingerprintMatch(0, 1)) {
			// XXX
			System.out.println("responses for <vega></vega> and </vega><vega> look different for "+ ctx.getPathState().getPath().getFullPath());
			ctx.responseChecks(1);
		}
		
		scheduleNext(ctx.getPathState());
		
	}
	
	private void scheduleNext(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		
		final String[] injectme = {
				"`true`", "`false`", "`uname`", 
				"\"`true`\"", "\"`false`\"", "\"`uname`\"",
				"'true'", "'false'", "'uname'" 
		};
		
		ctx.submitMultipleAlteredRequests(injectHandler3, injectme, true);
		
	}

}
