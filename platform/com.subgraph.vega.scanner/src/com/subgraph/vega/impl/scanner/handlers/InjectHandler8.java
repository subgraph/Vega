package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class InjectHandler8 implements ICrawlerResponseProcessor {
	private final ICrawlerResponseProcessor injectHandler9 = new InjectHandler9();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;

		if(ctx.hasModuleFailed())
			return;
		if(response.isFetchFail()) {
			ctx.error(request, response, "during format string attacks");
			ctx.setModuleFailed();
			scheduleNext(ctx.getPathState());
			return;
		}
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		if(ctx.incrementResponseCount() < 2)
			return;
		
		if(!ctx.isFingerprintMatch(0, 1)) {
			System.out.println("response to %dn%dn%dn... different than to %nd%nd%nd...");
			ctx.responseChecks(1);

		}
		scheduleNext(ctx.getPathState());
	}

	private void scheduleNext(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		
		
		final String[] injectme = { "-0000012345", "-2147483649", "-2147483648", 
				"0000012345", "2147483647", "2147483648", "4294967295",  "4294967296", "0000023456"};
		ctx.submitMultipleAlteredRequests(injectHandler9, injectme);
	}
}
