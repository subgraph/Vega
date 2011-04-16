package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.http.requests.custom.HttpEntityEnclosingRawRequest;
import com.subgraph.vega.http.requests.custom.HttpRawRequest;

public class ProxyRequestHandler implements HttpRequestHandler {

	private final static String[] HOP_BY_HOP_HEADERS = {
		HTTP.CONTENT_LEN, HTTP.TRANSFER_ENCODING, HTTP.CONN_DIRECTIVE, 
		"Keep-Alive", "Proxy-Authenticate", "TE", "Trailers", "Upgrade"
	};

	private final HttpProxy httpProxy;
	private final IHttpRequestEngine requestEngine;

	ProxyRequestHandler(HttpProxy httpProxy, IHttpRequestEngine requestEngine) {
		this.httpProxy = httpProxy;
		this.requestEngine = requestEngine;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		final ProxyTransaction transaction = new ProxyTransaction(requestEngine, context);
		context.setAttribute(HttpProxy.PROXY_HTTP_TRANSACTION, transaction);

		try {
			if (handleRequest(transaction, request) == false) {
				response.setStatusCode(503);
				transaction.signalComplete();
				return;
			}

			HttpUriRequest uriRequest = getUriRequest(transaction);
			BasicHttpContext ctx = new BasicHttpContext();
			IHttpResponse r = requestEngine.sendRequest(uriRequest, ctx);
			if(r == null) {
				response.setStatusCode(503);
				transaction.signalComplete();
				return;
			}

			if (handleResponse(transaction, r) == false) {
				response.setStatusCode(503);
				transaction.signalComplete();
				return;
			}

			HttpResponse httpResponse = copyResponse(r.getRawResponse());
			removeHopByHopHeaders(httpResponse);
			response.setStatusLine(httpResponse.getStatusLine());
			response.setHeaders(httpResponse.getAllHeaders());
			response.setEntity(httpResponse.getEntity());
		} catch (InterruptedException e) {
			response.setStatusCode(503);
			e.printStackTrace();
		} finally {
			transaction.signalComplete();
		}
	}

	private HttpEntity copyEntity(HttpEntity entity) {
		try {
			if(entity == null) {
				return null;
			}
			final ByteArrayEntity newEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity));
			newEntity.setContentEncoding(entity.getContentEncoding());
			newEntity.setContentType(entity.getContentType());
			return newEntity;
		} catch (IOException e) {
			return null;
		}
	}

	private HttpRequest copyRequest(HttpRequest request) {
		if (request instanceof HttpEntityEnclosingRequest) {
			BasicHttpEntityEnclosingRequest cp = new BasicHttpEntityEnclosingRequest(request.getRequestLine());
			cp.setEntity(copyEntity(((HttpEntityEnclosingRequest) request).getEntity()));
			cp.setHeaders(request.getAllHeaders());
			cp.setParams(request.getParams());
			return cp;
		} else {
			BasicHttpRequest cp = new BasicHttpRequest(request.getRequestLine());
			cp.setHeaders(request.getAllHeaders());
			cp.setParams(request.getParams());
			return cp;
		}
	}

	private HttpUriRequest copyToUriRequest(HttpRequest request) {
		final RequestLine requestLine = request.getRequestLine(); 
		final URI requestUri = stringToURI(requestLine.getUri());
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRawRequest cp = new HttpEntityEnclosingRawRequest(null, requestLine.getMethod(), requestUri);
			cp.setEntity(copyEntity(((HttpEntityEnclosingRequest) request).getEntity()));
			cp.setHeaders(request.getAllHeaders());
			cp.setParams(request.getParams());
			return cp;
		} else {
			HttpRawRequest cp = new HttpRawRequest(null, requestLine.getMethod(), requestUri);
			cp.setHeaders(request.getAllHeaders());
			cp.setParams(request.getParams());
			return cp;
		}
	}

	private HttpResponse copyResponse(HttpResponse originalResponse) {
		HttpResponse r = new BasicHttpResponse(originalResponse.getStatusLine());
		r.setHeaders(originalResponse.getAllHeaders());
		r.setEntity(originalResponse.getEntity());
		return r;
	}

	private void removeHopByHopHeaders(HttpMessage message) {
		for(String hdr: HOP_BY_HOP_HEADERS) { 
			message.removeHeaders(hdr);
		}
	}

	private URI stringToURI(String uriString) {
		try {
			return new URI(uriString);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Could not parse URI string "+ uriString, e);
		}
	}

	private boolean handleRequest(ProxyTransaction transaction, HttpRequest request) throws InterruptedException {
		removeHopByHopHeaders(request);
		transaction.setRequest(copyRequest(request));
		if (httpProxy.handleTransaction(transaction) == true) {
			return transaction.getForward();
		} else {
			return true;
		}
	}

	private HttpUriRequest getUriRequest(ProxyTransaction transaction) {
		HttpUriRequest uriRequest = transaction.getUriRequest();
		if (uriRequest == null) {
			uriRequest = copyToUriRequest(transaction.getRequest());
			transaction.setUriRequest(uriRequest);
		}
		return uriRequest;
	}

	private boolean handleResponse(ProxyTransaction transaction, IHttpResponse response) throws InterruptedException {
		transaction.setResponse(response);
		if (httpProxy.handleTransaction(transaction) == true) {
			return transaction.getForward();
		} else {
			return true;
		}
	}
}
