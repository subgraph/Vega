package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestEngineException;
import com.subgraph.vega.api.scanner.IScanProbeResult;

public class ScanProbe {
	private final static int MAX_REDIRECT_COUNT = 5;
	private final URI targetURI;
	private final IHttpRequestEngine requestEngine;
	
	ScanProbe(URI targetURI, IHttpRequestEngine requestEngine) {
		this.targetURI = targetURI;
		this.requestEngine = requestEngine;
	}
	
	IScanProbeResult runProbe() {
		final HttpGet request = new HttpGet(targetURI);
		try {
			IHttpResponse response = requestEngine.sendRequest(request);
			return processFirstProbeResponse(targetURI, response);
		} catch (RequestEngineException e) {
			return ScanProbeResult.createConnectFailedResult(e.getMessage());
		}
	}
		
	private IScanProbeResult processFirstProbeResponse(URI targetURI, IHttpResponse response) {
		if(isResponseRedirect(response)) {
			return processRedirect(targetURI, response);
		}
		return ScanProbeResult.createOkResult();
	}

	private boolean isResponseRedirect(IHttpResponse response) {
		final int code = response.getResponseCode();
		return (code == 301 || code == 302 || code == 303 || code == 307);
	}

	private IScanProbeResult processRedirect(URI originalTarget, IHttpResponse response) {
		int redirectCount = 1;
		
		while(redirectCount < MAX_REDIRECT_COUNT) {
			URI location = getLocationURI(response);
			if(location == null) {
				final String msg = "Target address redirected to a location which could not be understood";
				return ScanProbeResult.createRedirectFailedResult(msg);
			}

			try {
				response = requestEngine.sendRequest(new HttpGet(location));
				if(!isResponseRedirect(response)) {
					return ScanProbeResult.createRedirectResult(location);
				}
			} catch (RequestEngineException e) {
				return ScanProbeResult.createRedirectFailedResult(createRedirectExceptionMessage(originalTarget, location, e));
			}
			redirectCount += 1;
		}
		return ScanProbeResult.createConnectFailedResult("Could not connect to target because maximum redirection limit reached");
	}

	private String createRedirectExceptionMessage(URI targetURI, URI location, Exception e) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Target address ");
		sb.append(targetURI.toString());
		sb.append(" redirected to address ");
		sb.append(location.toString());
		sb.append(" which was not reachable");
		sb.append(e.getMessage());
		return sb.toString();
	}
	
	private URI getLocationURI(IHttpResponse response) {
		final Header locationHeader = response.getRawResponse().getFirstHeader("Location");
		if(locationHeader == null) {
			return null;
		}
		final String location = locationHeader.getValue();
		try {
			return new URI(location);
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
