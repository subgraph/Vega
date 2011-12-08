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
package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestTask;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;
import com.subgraph.vega.api.http.requests.RequestEngineException;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

class HttpRequestTask implements IHttpRequestTask, Callable<IHttpResponse> {
	private final static Logger logger = Logger.getLogger("request-engine");
	private Future<IHttpResponse> future;
	private final HttpRequestEngine requestEngine;
	private final HttpClient client;
	private final RateLimiter rateLimit;
	private final HttpUriRequest request;
	private final IRequestOrigin requestOrigin;
	private final HttpContext context;
	private final IHttpRequestEngineConfig config;
	private final IHTMLParser htmlParser;

	public HttpRequestTask(HttpRequestEngine requestEngine, HttpClient client, RateLimiter rateLimit, HttpUriRequest request, IRequestOrigin requestOrigin, HttpContext context, IHttpRequestEngineConfig config, IHTMLParser htmlParser) {
		this.requestEngine = requestEngine;
		this.client = client;
		this.rateLimit = rateLimit;
		this.request = request;
		this.requestOrigin = requestOrigin;
		this.context = context;
		this.config = config;
		this.htmlParser = htmlParser;
	}

	public void setFuture(Future<IHttpResponse> future) {
		this.future = future;
	}
	
	@Override
	public IHttpRequestEngine getRequestEngine() {
		return requestEngine;
	}

	@Override
	public void abort() {
		request.abort();
	}

	@Override
	public HttpUriRequest getRequest() {
		return request;
	}

	@Override
	public IHttpResponse get() throws RequestEngineException {
		try {
			return future.get();
		} catch (InterruptedException e) {
			logger.info("Request "+ request.getURI() +" was interrupted before completion");
		} catch (ExecutionException e) {
			throw translateException(request, e.getCause());
		}
		return null;
	}

	@Override
	public IHttpResponse call() throws Exception {
		if(config.getForceIdentityEncoding())
			request.setHeader(HTTP.CONTENT_ENCODING, HTTP.IDENTITY_CODING);

		if(rateLimit != null)
			rateLimit.maybeDelayRequest();

		requestEngine.addRequestInProgress(this);
		final long start = System.currentTimeMillis();
		final long elapsed;
		final HttpResponse httpResponse;
		try {
			httpResponse = client.execute(request, context);
			elapsed = System.currentTimeMillis() - start;
		} finally {
			requestEngine.removeRequestInProgress(this);
		}

		final HttpEntity entity = httpResponse.getEntity();
		if(entity != null) {
			if(config.getMaximumResponseKilobytes() > 0 && entity.getContentLength() > (config.getMaximumResponseKilobytes() * 1024)) {
				logger.warning("Aborting request "+ request.getURI().toString() +" because response length "+ entity.getContentLength() + " exceeds maximum length of "+ config.getMaximumResponseKilobytes() +" kb.");
				request.abort();
				httpResponse.setEntity(createEmptyEntity());
			}

			final HttpEntity newEntity = processEntity(httpResponse, entity);
			httpResponse.setEntity(newEntity);
		}
		final HttpHost host = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		final HttpRequest sentRequest = (HttpRequest) context.getAttribute(HttpRequestEngine.VEGA_SENT_REQUEST);
		
		final IHttpResponse response = new EngineHttpResponse(
				request.getURI(), host,  
				(sentRequest == null) ? (request) : (sentRequest), requestOrigin,
				httpResponse, 
				elapsed, 
				htmlParser
		);

		for(IHttpResponseProcessor p: config.getResponseProcessors()) {
			p.processResponse(response.getOriginalRequest(), response, context);
		}
		
		return response;
	}

	private HttpEntity processEntity(HttpResponse response, HttpEntity entity) throws IOException {
		if(entity == null)
			return null;

		if(isGzipEncoded(entity) && config.getDecompressGzipEncoding())
			return processGzipEncodedEntity(response, entity);

		final InputStream input = entity.getContent();

		if(input == null) {
			return createEmptyEntity();
		}

		String contentType = (entity.getContentType() == null) ? (null) : (entity.getContentType().getValue());
		String contentEncoding = (entity.getContentEncoding() == null) ? (null) : (entity.getContentEncoding().getValue());
		RepeatableStreamingEntity e =  new RepeatableStreamingEntity(input, entity.getContentLength(), false, entity.isChunked(), contentType, contentEncoding);
		if(config.getMaximumResponseKilobytes() > 0) {
			e.setMaximumInputKilobytes(config.getMaximumResponseKilobytes());
		}
		return e;
	}

	private HttpEntity createEmptyEntity() {
		return new ByteArrayEntity(new byte[0]);
	}

	private HttpEntity processGzipEncodedEntity(HttpResponse response, HttpEntity entity) throws IOException {
		final InputStream input = entity.getContent();
		if(input == null) {
			response.setHeader(HTTP.CONTENT_LEN, "0");
			return new ByteArrayEntity(new byte[0]);
		}
		final InputStream gzipInput = new GZIPInputStream(input);
		response.removeHeaders(HTTP.CONTENT_ENCODING);
		String contentType = (entity.getContentType() == null) ? (null) : (entity.getContentType().getValue());
		RepeatableStreamingEntity newEntity = new RepeatableStreamingEntity(gzipInput, -1, true, entity.isChunked(), contentType, null);
		response.setHeader(HTTP.CONTENT_LEN, Long.toString(newEntity.getContentLength()));
		if(config.getMaximumResponseKilobytes() > 0) {
			newEntity.setMaximumInputKilobytes(config.getMaximumResponseKilobytes());
		}
		return newEntity;
	}

	private boolean isGzipEncoded(HttpEntity entity) {
		if(entity == null)
			return false;
		final Header ceh = entity.getContentEncoding();
		if(ceh == null)
			return false;
		for(HeaderElement element : ceh.getElements()) {
			if("gzip".equalsIgnoreCase(element.getName()))
				return true;
		}
		return false;
	}

	private RequestEngineException translateException(HttpUriRequest request, Throwable ex) {
		final StringBuilder sb = new StringBuilder();
		if(ex instanceof IOException) {
			sb.append("Network problem");
		} else if(ex instanceof HttpException) {
			sb.append("Protocol problem");
		} else {
			sb.append("Unknown problem");
		}
		sb.append(" while retrieving URI ");
		sb.append(request.getURI().toString());
		sb.append(" [");
		sb.append(ex.getMessage());
		sb.append("]");
		return new RequestEngineException(sb.toString(), ex);
	}

}
