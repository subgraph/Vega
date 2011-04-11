package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.apache.http.protocol.HttpService;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

/**
 * Specialized HttpService which handles SSL connections.
 */
public class VegaHttpService {

	private final IHttpRequestEngine requestEngine;
	private final HttpService delegatedHttpService;

	public VegaHttpService(IHttpRequestEngine requestEngine, HttpProcessor proc, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory) {
		this.delegatedHttpService = new HttpService(proc, connStrategy, responseFactory);
		this.requestEngine = requestEngine;
	}

	public void setHandlerResolver(final HttpRequestHandlerResolver handlerResolver) {
		delegatedHttpService.setHandlerResolver(handlerResolver);
	}

	public void setParams(final HttpParams params) {
		delegatedHttpService.setParams(params);
	}

	public void handleRequest(final HttpServerConnection conn, final HttpContext context) throws IOException, HttpException { 
		VegaHttpServerConnection vconn = (VegaHttpServerConnection) conn;
		HttpRequest request = vconn.receiveRequestHeader();
		if(!isConnectMethodRequest(request)) {
			vconn.setCachedRequest(request);
			delegatedHttpService.handleRequest(conn, context);
			return;
		}
	}

	private boolean isConnectMethodRequest(HttpRequest request) {
		final String method = request.getRequestLine().getMethod();
		return (method != null && method.equalsIgnoreCase("CONNECT"));
	}
}
