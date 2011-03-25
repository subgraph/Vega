package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class InjectHandler6 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler7 = new InjectHandler7();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;

		if(response.getRawResponse().containsHeader("Vega-Inject")) {
			System.out.println("Problem injected Vega-Inject header");
		}
		
		if(ctx.getCurrentIndex() != 1)
			return;

		final IModuleContext newCtx = ctx.getPathState().createModuleContext();
		boolean isNum = isNumericParameterValue(ctx.getPathState());

		if(isNum) {
			newCtx.submitAlteredRequest(injectHandler7, "-0", true, 0);
			newCtx.submitAlteredRequest(injectHandler7, "-0-0", true, 1);
			newCtx.submitAlteredRequest(injectHandler7, "-0-9", true, 2);
		} else {
			newCtx.submitAlteredRequest(injectHandler7, "9-8", 0);
			newCtx.submitAlteredRequest(injectHandler7, "8-7", 1);
			newCtx.submitAlteredRequest(injectHandler7, "9-1", 2);
		}
		
		submitRequest(newCtx, 3, "\\\'\\\"");
		submitRequest(newCtx, 4, "\'\"");
		submitRequest(newCtx, 5, "\\\\\'\\\\\"");
		
		if(isNum) {
			newCtx.submitAlteredRequest(injectHandler7, " - 0 - 0", true, 6);
			newCtx.submitAlteredRequest(injectHandler7, " 0 0 - -", true, 7);
		} else {
			newCtx.submitAlteredRequest(injectHandler7, "9 - 1", 6);
			newCtx.submitAlteredRequest(injectHandler7, "9 1 -", 7);
		}
	}
	
	private boolean isNumericParameterValue(IPathState ps) {
		if(!ps.isParametric())
			return false;
		final NameValuePair p = ps.getFuzzableParameter();
		if(p == null || p.getValue() == null)
			return false;
		final String v = p.getValue();
		final String chars = "01234567890.+-";
		for(int i = 0; i < v.length(); i++) {
			if(chars.indexOf(v.charAt(i)) == -1)
				return false;
		}
		return true;
	}
	
	private void submitRequest(IModuleContext ctx, int n, String s) {
		final HttpUriRequest req = ctx.getPathState().createAlteredRequest(s, true);
		final String s1 = "vega"+s;
		final String s2 = s1 + ",en";
		req.addHeader("User-Agent", s1);
		req.addHeader("Referer", s1);
		req.addHeader("Accept-Language", s2);
		ctx.submitRequest(req, injectHandler7, n);
	}

}
