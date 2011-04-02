package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class UnknownProcessor implements ICrawlerResponseProcessor {
	private final ParametricCheckHandler parametricChecks = new ParametricCheckHandler();
	private final ICrawlerResponseProcessor fetchFileProcessor = new FileProcessor();
	private final ICrawlerResponseProcessor unknownCheck = new UnknownCheckHandler();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		final IPathState ps = ctx.getPathState();
		ps.setResponse(response);
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "during initial resource fetch");
			return;
		}
		ps.getPath().setVisited(true);

		final IPageFingerprint fp = response.getPageFingerprint();
		final IPathState par = ps.get404Parent();
		final int rcode = response.getResponseCode();

		if((par == null && rcode == 404) || (ps.hasParent404Fingerprint(fp))) {
			ps.setPageMissing();
			ps.unlockChildren();
			parametricChecks.initialize(ps);
			return;
		}
		
		if(par != null && !response.getBodyAsString().isEmpty() && rcode == 200 && fp.isSame(par.getUnknownFingerprint())) {
			ps.getPath().setPathType(PathType.PATH_FILE);
			fetchFileProcessor.processResponse(crawler, request, response, ctx);
			return;
		}
		
		
		if(par != null && rcode >= 300 && rcode < 400 && fp.isSame(par.getUnknownFingerprint()) && fp.isSame(par.getPathFingerprint())) {
			ps.getPath().setPathType(PathType.PATH_FILE);

			fetchFileProcessor.processResponse(crawler, request, response, argument);
			return;
		}

		
		final IModuleContext newCtx = ps.createModuleContext();
		newCtx.submitAlteredRequest(unknownCheck, "/", 0);
		newCtx.submitAlteredRequest(unknownCheck, "/abc123/", 1);
	}

}
