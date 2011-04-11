package com.subgraph.vega.internal.http.proxy;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponseFactory;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpService;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

/**
 * Specialized HttpService which handles SSL connections.
 */
public class VegaHttpService extends HttpService {

	private final IHttpRequestEngine requestEngine;
	
	public VegaHttpService(IHttpRequestEngine requestEngine, HttpProcessor proc, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory) {
		super(proc, connStrategy, responseFactory);
		this.requestEngine = requestEngine;
	}

}
