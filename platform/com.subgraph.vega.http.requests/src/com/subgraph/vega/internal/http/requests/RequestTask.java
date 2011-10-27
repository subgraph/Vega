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
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
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
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

class RequestTask implements Callable<IHttpResponse> {
	private final static Logger logger = Logger.getLogger("request-engine");

	private final HttpClient client;
	private final RateLimiter rateLimit;
	private final HttpUriRequest request;
	private final IRequestOrigin requestOrigin;
	private final HttpContext context;
	private final IHttpRequestEngineConfig config;
	private final IHTMLParser htmlParser;

	RequestTask(HttpClient client, RateLimiter rateLimit, HttpUriRequest request, IRequestOrigin requestOrigin, HttpContext context, IHttpRequestEngineConfig config, IHTMLParser htmlParser) {
		this.client = client;
		this.rateLimit = rateLimit;
		this.request = request;
		this.requestOrigin = requestOrigin;
		this.context = context;
		this.config = config;
		this.htmlParser = htmlParser;
	}

	@Override
	public IHttpResponse call() throws Exception {
		if(config.getForceIdentityEncoding())
			request.setHeader(HTTP.CONTENT_ENCODING, HTTP.IDENTITY_CODING);

		if(rateLimit != null)
			rateLimit.maybeDelayRequest();
		
		final long start = System.currentTimeMillis();
		final HttpResponse httpResponse = client.execute(request, context);
		final long elapsed = System.currentTimeMillis() - start;

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
}
