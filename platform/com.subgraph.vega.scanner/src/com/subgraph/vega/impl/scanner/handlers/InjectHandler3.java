package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class InjectHandler3 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler4 = new InjectHandler4();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		
		final IModuleContext ctx = (IModuleContext) argument;

		if(ctx.hasModuleFailed())
			return;
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "during path based shell injection attacks");
			ctx.setModuleFailed();
			scheduleNext(ctx.getPathState());
			return;
		}
		
		ctx.addRequestResponse(ctx.getCurrentIndex(), request, response);
		
		if(ctx.incrementResponseCount() < 9)
			return;
		final String fullPath = ctx.getPathState().getPath().getFullPath();
		if(ctx.isFingerprintMatch(0, 1) && !ctx.isFingerprintMatch(0, 2)) {
			// XXX
			System.out.println("1 responses to `true` and `false` are different than `uname` for "+ fullPath);
			ctx.responseChecks(2);

		}
		if(ctx.isFingerprintMatch(3, 4) && !ctx.isFingerprintMatch(3, 5)) {
			// XXX
			System.out.println("2 responses to `true` and `false` are different than `uname` for "+ fullPath);
			ctx.responseChecks(5);
			
			
		}
		if(ctx.isFingerprintMatch(6, 7) && !ctx.isFingerprintMatch(6, 8)) {
			// XXX
			System.out.println("3 responses to `true` and `false` are different than `uname` for "+ fullPath);
			ctx.responseChecks(8);

			
		}
		scheduleNext(ctx.getPathState());
	}
	
	private void scheduleNext(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		
		final int xid1 = ps.allocateXssId();
		final int xid2 = ps.allocateXssId();
		final String tag1 = ps.createXssTag(xid1);
		final String tag2 = ps.createXssTag(".htaccess.aspx", xid2);
		
		
		HttpUriRequest req1 = ps.createAlteredRequest(tag1, true);
		if(req1 != null) {
			ps.registerXssRequest(req1, xid1);
			req1.addHeader("Referer", tag1);
			ctx.submitRequest(req1, injectHandler4, 0);
		}
		
		final HttpUriRequest req2 = ps.createAlteredRequest(tag2, false);
		if(req2 != null) {
			ps.registerXssRequest(req2, xid2);
			ctx.submitRequest(req2, injectHandler4, 1);
		}
	}

}
