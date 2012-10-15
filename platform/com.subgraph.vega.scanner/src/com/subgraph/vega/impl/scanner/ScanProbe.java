/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.impl.scanner;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestEngineException;
import com.subgraph.vega.api.scanner.IScanProbeResult;

public class ScanProbe {
	private final Logger logger = Logger.getLogger("scanner");
	private final static int MAX_REDIRECT_COUNT = 5;
	private final URI targetURI;
	private final IHttpRequestEngine requestEngine;
	private volatile HttpGet currentRequest;
	
	ScanProbe(URI targetURI, IHttpRequestEngine requestEngine) {
		this.targetURI = targetURI;
		this.requestEngine = requestEngine;
	}
	
	IScanProbeResult runProbe() {
		currentRequest = new HttpGet(targetURI);
		try {
			IHttpResponse response = requestEngine.sendRequest(currentRequest).get();
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
				currentRequest = new HttpGet(location);
				response = requestEngine.sendRequest(currentRequest).get();
				try {
					EntityUtils.consume(response.getRawResponse().getEntity());
				} catch (IOException e) {
					// shouldn't matter
				}
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
		return response.getRequestUri().resolve(locationHeader.getValue());
		/*
		
		final String location = locationHeader.getValue();
		try {
			return new URI(location);
		} catch (URISyntaxException e) {
			logger.log(Level.WARNING,"URI Syntax Exception on: "+locationHeader);
			return null;
		}
		*/
	}
	    
		
	void abort() {
		final HttpGet get = currentRequest;
		if(get != null) {
			get.abort();
		}
	}
}
