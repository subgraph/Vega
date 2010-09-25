package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

class RequestTask implements Callable<HttpResponse> {

	private final HttpClient client;
	private final HttpUriRequest request;
	private final HttpContext context;
	private final HttpRequestEngineConfig config;

	RequestTask(HttpClient client, HttpUriRequest request, HttpContext context, HttpRequestEngineConfig config) {
		this.client = client;
		this.request = request;
		this.context = context;
		this.config = config;
	}

	@Override
	public HttpResponse call() throws Exception {
		if(config.forceIdentityEncoding())
			request.setHeader(HTTP.CONTENT_ENCODING, HTTP.IDENTITY_CODING);

		final HttpResponse response = client.execute(request, context);

		final HttpEntity entity = response.getEntity();

		if(entity == null)
			return response;

		if(entity != null) {
			final HttpEntity newEntity = processEntity(response, entity);
			response.setEntity(newEntity);
		}
		return response;
	}

	private HttpEntity processEntity(HttpResponse response, HttpEntity entity) throws IOException {
		if(entity == null)
			return null;

		if(isGzipEncoded(entity) && config.decompressGzipEncoding())
			return processGzipEncodedEntity(response, entity);

		final InputStream input = entity.getContent();

		if(input == null)
			return new ByteArrayEntity(new byte[0]);

		String contentType = (entity.getContentType() == null) ? (null) : (entity.getContentType().getValue());
		String contentEncoding = (entity.getContentEncoding() == null) ? (null) : (entity.getContentEncoding().getValue());
		return new RepeatableStreamingEntity(input, entity.getContentLength(), entity.isChunked(), contentType, contentEncoding);
	}

	private HttpEntity processGzipEncodedEntity(HttpResponse response, HttpEntity entity) throws IOException {
		final InputStream input = entity.getContent();
		if(input == null)
			return new ByteArrayEntity(new byte[0]);
		final InputStream gzipInput = new GZIPInputStream(input);
		response.removeHeaders(HTTP.CONTENT_ENCODING);
		String contentType = (entity.getContentType() == null) ? (null) : (entity.getContentType().getValue());
		return new RepeatableStreamingEntity(gzipInput, -1, entity.isChunked(), contentType, null);
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
