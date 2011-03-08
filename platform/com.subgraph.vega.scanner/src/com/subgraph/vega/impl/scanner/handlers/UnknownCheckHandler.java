package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.impl.scanner.ScanRequestData;
import com.subgraph.vega.impl.scanner.state.PageFingerprint;
import com.subgraph.vega.impl.scanner.state.PathState;

public class UnknownCheckHandler implements ICrawlerResponseProcessor {

	private final ICrawlerResponseProcessor fetchFileProcessor = new FileProcessor();
	private final ICrawlerResponseProcessor fetchDirProcessor = new DirectoryProcessor();
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		
		final ScanRequestData data = (ScanRequestData) argument;
		final PathState ps = data.getPathState();
		final IHttpResponse stateRes = ps.getResponse();
		final HttpUriRequest stateReq = ps.createRequest();
		if(response.isFetchFail()) {
			ps.error(request, response, "during node type check");
			scheduleNext(crawler, stateReq, stateRes, data, ps);
			return;
		}
		
		ps.addMiscRequestResponse(data.getFlag(), request, response);
		if(ps.incrementMiscCount() < 2)
			return;
		
		
		final IHttpResponse res0 = ps.getMiscResponse(0);
		final HttpUriRequest req0 = ps.getMiscRequest(0);
		final PageFingerprint fp0 = ps.getMiscFingerprint(0);
		
		if(ps.miscFingerprintsMatch(0, 1) && ps.miscFingerprintMatchesPath(1)) {
			ps.getPath().setPathType(PathType.PATH_FILE);
			scheduleNext(crawler, stateReq, stateRes, data, ps);
			return;
		}
		
		if(ps.miscFingerprintMatchesPath(0)) {
			assumeDir(crawler, stateReq, stateRes, data, ps);
			return;
		}

		final HttpResponse r = ps.getResponse().getRawResponse();
		
		final int code = ps.getResponse().getResponseCode();
		if(response.getResponseCode() == 404 && code >= 300 && code < 400) {
			final Header locationHeader = r.getFirstHeader("Location");
			if(locationHeader != null) {
				final String loc = locationHeader.getValue();
				if(loc != null && loc.equalsIgnoreCase(req0.getURI().toString())) {
					ps.setSureDirectory();
					assumeDir(crawler, stateReq, stateRes, data, ps);
					return;
				}
			}
		}
		
			
		final PathState p404 = ps.get404Parent();
			
		if( ((p404 == null) && res0.getResponseCode() == 404) ||  ps.hasParent404Fingerprint(fp0) || (code  < 300 && res0.getResponseCode() >= 300 && ps.getResponse().getBodyAsString().length() > 0)) {
			ps.getPath().setPathType(PathType.PATH_FILE);
			scheduleNext(crawler, stateReq, stateRes, data, ps);
		}
	}
	
	private void assumeDir(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, ScanRequestData data, PathState ps) {
		// XXX no_505_dir check
		ps.debug("assuming "+ request.getURI() + " is directory");
		ps.getPath().setPathType(PathType.PATH_DIRECTORY);
		if(ps.getResponse() != null) 
			ps.setUnknownFingerprint(ps.getPathFingerprint());
		
		ps.setResponse(response);
		scheduleNext(crawler, request, response, data, ps);
	}
	private void scheduleNext(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, ScanRequestData data, PathState ps) {
		if(ps.getPath().getPathType() == PathType.PATH_DIRECTORY || ps.getPath().getParentPath() == null) {
			fetchDirProcessor.processResponse(crawler, request, response, data);
		} else {
			ps.debug("Assuming "+ request.getURI() +" is file");
			fetchFileProcessor.processResponse(crawler, request, response, data);
		}
	}
}
