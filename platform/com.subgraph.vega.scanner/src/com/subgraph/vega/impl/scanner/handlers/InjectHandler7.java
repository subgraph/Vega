package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class InjectHandler7 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler8 = new InjectHandler8();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		
		final IModuleContext ctx = (IModuleContext) argument;

		if(ctx.hasModuleFailed())
			return;

		if(response.isFetchFail()) {
			ctx.error(request, response, "during SQL injection attacks");
			ctx.setModuleFailed();
			
			scheduleNext(ctx.getPathState());
			return;
		}
		
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		if(ctx.incrementResponseCount() < 8)
			return;
		
		if(ctx.isFingerprintMatch(0, 1) && !ctx.isFingerprintMatch(0, 2)) {
			System.out.println("response suggests arithmetic evaluation on server side (type 1)");
			ctx.responseChecks(0);
			ctx.responseChecks(2);
		}
		
		if(ctx.isFingerprintMatch(1, 6) && !ctx.isFingerprintMatch(6, 7)) {
			System.out.println("response suggests arithmetic evaluation on server side (type 2)");
			ctx.responseChecks(6);
			ctx.responseChecks(7);
		}
		
		if(!ctx.isFingerprintMatch(3, 4) && !ctx.isFingerprintMatch(3, 5)) {
			System.out.println("response to '\" different than to \\'\\\"");
			ctx.responseChecks(3);
			ctx.responseChecks(4);
		}
		
		scheduleNext(ctx.getPathState());
	}
	
	private void scheduleNext(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		ctx.submitMultipleAlteredRequests(injectHandler8, new String[] { "vega%dn%dn%dn%dn%dn%dn%dn%dn", "vega%nd%nd%nd%nd%nd%nd%nd%nd"});
	}

}
