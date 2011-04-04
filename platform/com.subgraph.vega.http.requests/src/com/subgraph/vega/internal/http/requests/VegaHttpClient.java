package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.DefaultedHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.EntityUtils;

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
		final HttpRequest newRequest = createNewRequest(originalRequest);
		final HttpContext ctx = new BasicHttpContext(originalContext);
		final HttpContext execCtx = new DefaultedHttpContext(ctx, defaultContext);
		newRequest.setParams(new DefaultedHttpParams(newRequest.getParams(), getParams()));
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
	
	private HttpRequest createNewRequest(HttpRequest originalRequest) {
		if(originalRequest instanceof HttpEntityEnclosingRequest)
			return createNewEntityRequest((HttpEntityEnclosingRequest) originalRequest);
		else
			return new BasicHttpRequest(originalRequest.getRequestLine());
	}
	
	private HttpRequest createNewEntityRequest(HttpEntityEnclosingRequest originalRequest) {
		final HttpEntityEnclosingRequest req = new BasicHttpEntityEnclosingRequest(originalRequest.getRequestLine());
		final HttpEntity originalEntity = originalRequest.getEntity();
		if(originalEntity != null)
			req.setEntity(copyEntity(originalEntity));
		return req;		
	}
	
	private HttpEntity copyEntity(HttpEntity entity) {
		if(entity == null)
			return null;
		try {
			final ByteArrayEntity newEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity));
			newEntity.setContentEncoding(entity.getContentEncoding());
			newEntity.setContentType(entity.getContentType());
			return newEntity;
		} catch (IOException e) {
			logger.log(Level.WARNING, "I/O error reading entity", e);
			return null;
		}
	}
}
