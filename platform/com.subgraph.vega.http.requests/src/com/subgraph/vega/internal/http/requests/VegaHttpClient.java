package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.DefaultedHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

public class VegaHttpClient extends DefaultHttpClient {
	private final Logger logger = Logger.getLogger("request-engine");
	private final HttpProcessor httpProcessor;
	private final HttpContext defaultContext;
	VegaHttpClient(ClientConnectionManager ccm, HttpParams params) {
		super(ccm, params);
		httpProcessor = getHttpProcessor();
		defaultContext = createHttpContext();
	}
	
	HttpRequest createProcessedRequest(HttpRequest originalRequest, HttpContext originalContext) {
		final HttpRequest newRequest = new BasicHttpRequest(originalRequest.getRequestLine());
		final HttpContext ctx = new BasicHttpContext(originalContext);
		final HttpContext execCtx = new DefaultedHttpContext(ctx, defaultContext);
		for(Header h: originalRequest.getAllHeaders()) 
			newRequest.addHeader(h.getName(), h.getValue());
			
		try {
			httpProcessor.process(newRequest, execCtx);
		} catch (HttpException e) {
			logger.log(Level.WARNING, "Unexpected protocol exception building processed request", e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Unexpected IOException building processed request", e);
		}
		return newRequest;
	}
}
