package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PageFingerprint;
import com.subgraph.vega.impl.scanner.state.PathState;

public class UnknownProcessor implements ICrawlerResponseProcessor {
	private final ParametricCheckHandler parametricChecks = new ParametricCheckHandler();
	private final ICrawlerResponseProcessor fetchFileProcessor = new FileProcessor();
	private final ICrawlerResponseProcessor unknownCheck = new UnknownCheckHandler();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		ps.debug("UnknownProcessor: "+ request.getMethod() + " "+ request.getURI());
		
		ps.setResponse(response);
		if(response.isFetchFail()) {
			ps.error(request, response, "during initial resource fetch");
			return;
		}

		final PageFingerprint fp = PageFingerprint.generateFromCodeAndString(response.getResponseCode(), response.getBodyAsString());
		final PathState par = ps.get404Parent();
		final int rcode = response.getResponseCode();

		if((par == null && rcode == 404) || (ps.hasParent404Fingerprint(fp))) {
			ps.setPageMissing();
			ps.unlockChildren();
			ps.debug("starting parametric on unknown "+ request.getURI());
			ps.unlockChildren();
			parametricChecks.init(ps);
			return;
		}
		
		if(par != null && !response.getBodyAsString().isEmpty() && rcode == 200 && fp.isSame(par.getUnknownFingerprint())) {
			ps.getPath().setPathType(PathType.PATH_FILE);
			ps.debug("Assuming "+ request.getURI() + " is file without check");
			fetchFileProcessor.processResponse(crawler, request, response, data);
			return;
		}
		
		
		if(par != null && rcode >= 300 && rcode < 400 && fp.isSame(par.getUnknownFingerprint()) && fp.isSame(par.getPathFingerprint())) {
			ps.getPath().setPathType(PathType.PATH_FILE);
			ps.debug("Assuming "+ request.getURI() + " is file without check");

			fetchFileProcessor.processResponse(crawler, request, response, argument);
			return;
		}

		ps.resetMiscData();
		ps.submitAlteredRequest(unknownCheck, "/", 0);
		ps.submitAlteredRequest(unknownCheck, "/abc123/", 1);
	}

}
