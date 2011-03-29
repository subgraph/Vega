package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;

public class InjectHandler5 implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor injectHandler6 = new InjectHandler6();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		
		final Header locationHeader = response.getRawResponse().getFirstHeader("Location");
		final String location = (locationHeader != null) ? (locationHeader.getValue()) : (null);
		if(location != null) {
			if(location.startsWith("http://vega.invalid/") || location.startsWith("//vega.invalid/")) {
				// XXX
				System.out.println("problem injected URL in 'Location' header");
			}
		}
		
		final Header refreshHeader = response.getRawResponse().getFirstHeader("Refresh");
		final String refresh = (refreshHeader != null) ? (refreshHeader.getValue()) : (null);
		if(refresh != null && refresh.contains("=")) {
			String val = refresh.substring(refresh.indexOf('='));
			char c = val.charAt(0);
			boolean semiSafe = false;
			if(c == '\'' || c == '"') {
				semiSafe = true;
				val = val.substring(1);
			}
			if(val.startsWith("http://vega.invalid/") || val.startsWith("//vega.invalid/")) {
				System.out.println("problem injected URL in 'Refresh' header");
			}
			if(val.startsWith("vega://") || (semiSafe && val.contains(";"))) {
				System.out.println("problem injected URL in 'Refresh' header");
			}
		}
		
		ctx.contentChecks(request, response);
		
		if(ctx.getCurrentIndex() != 2)
			return;
		
		IModuleContext newCtx = ctx.getPathState().createModuleContext();
		newCtx.submitMultipleAlteredRequests(injectHandler6, new String[] {"bogus\nVega-Inject:bogus", "bogus\rVega-Inject:bogus" }, true);
	}

}
