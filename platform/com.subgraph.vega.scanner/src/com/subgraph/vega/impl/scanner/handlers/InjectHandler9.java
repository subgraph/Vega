package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;

public class InjectHandler9 implements ICrawlerResponseProcessor {

	private final EndInjectionChecks end = new EndInjectionChecks();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		
		if(ctx.hasModuleFailed())
			return;
		
		if(response.isFetchFail()) {
			ctx.setModuleFailed();
			end.endChecks(ctx.getPathState());
			return;
		}
		
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		
		if(ctx.incrementResponseCount() < 9)
			return;
		
		if(!ctx.isFingerprintMatch(3, 8)) {
			end.endChecks(ctx.getPathState());
			return;
		}
		
		if(!ctx.isFingerprintMatch(0, 1)) {
			System.out.println("response to -(2^31-1) different than to -12345");
			ctx.responseChecks(1);
		}
		
		if(!ctx.isFingerprintMatch(0, 2)) {
			System.out.println("response to -2^31 different than to -12345");
			ctx.responseChecks(2);
		}

		if(!ctx.isFingerprintMatch(3, 4)) {
			System.out.println("response to 2^31-1 different than to 12345");
			ctx.responseChecks(4);
		}

		if(!ctx.isFingerprintMatch(3, 5)) {
			System.out.println("response to 2^31 different than to 12345");
			ctx.responseChecks(5);
		}

		if(!ctx.isFingerprintMatch(3, 6)) {
			System.out.println("response to 2^32-1 different than to 12345");
			ctx.responseChecks(6);
		}

		if(!ctx.isFingerprintMatch(3, 7)) {
			System.out.println("response to 2^32 different than to 12345");
			ctx.responseChecks(7);
		}

		end.endChecks(ctx.getPathState());
	}
	

}
