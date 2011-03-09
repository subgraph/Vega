package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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

class RequestTask implements Callable<IHttpResponse> {

	private final HttpClient client;
	private final HttpUriRequest request;
	private final HttpContext context;
	private final IHttpRequestEngineConfig config;
	private final IHTMLParser htmlParser;

	RequestTask(HttpClient client, HttpUriRequest request, HttpContext context, IHttpRequestEngineConfig config, IHTMLParser htmlParser) {
		this.client = client;
		this.request = request;
		this.context = context;
		this.config = config;
		this.htmlParser = htmlParser;
	}

	@Override
	public IHttpResponse call() throws Exception {
		if(config.getForceIdentityEncoding())
			request.setHeader(HTTP.CONTENT_ENCODING, HTTP.IDENTITY_CODING);
		if(config.getCookieString() != null && !config.getCookieString().isEmpty())
			request.setHeader("Cookie",config.getCookieString());

		final HttpResponse httpResponse = client.execute(request, context);

		final HttpEntity entity = httpResponse.getEntity();

		if(entity != null) {
			final HttpEntity newEntity = processEntity(httpResponse, entity);
			httpResponse.setEntity(newEntity);
		}
		final HttpHost host = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		final IHttpResponse response = new EngineHttpResponse(request.getURI(), host, request, httpResponse, htmlParser);
		for(IHttpResponseProcessor p: config.getResponseProcessors())
			p.processResponse(request, response, context);
		
		return response;
	}

	private HttpEntity processEntity(HttpResponse response, HttpEntity entity) throws IOException {
		if(entity == null)
			return null;

		if(isGzipEncoded(entity) && config.getDecompressGzipEncoding())
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
