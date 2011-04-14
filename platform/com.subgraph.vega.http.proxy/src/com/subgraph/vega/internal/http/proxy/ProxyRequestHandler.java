package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class ProxyRequestHandler implements HttpRequestHandler {

	private final static String[] HOP_BY_HOP_HEADERS = {
		HTTP.CONTENT_LEN, HTTP.TRANSFER_ENCODING, HTTP.CONN_DIRECTIVE, 
		"Keep-Alive", "Proxy-Authenticate", "TE", "Trailers", "Upgrade"
	};

	private final HttpProxy httpProxy;
	private final IHttpRequestEngine requestEngine;
	private final UriRequestCreator uriRequestCreator;

	ProxyRequestHandler(HttpProxy httpProxy, IHttpRequestEngine requestEngine) {
		this.httpProxy = httpProxy;
		this.requestEngine = requestEngine;
		this.uriRequestCreator = new UriRequestCreator(false);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		final ProxyTransaction transaction = new ProxyTransaction(context);
		context.setAttribute(HttpProxy.PROXY_HTTP_TRANSACTION, transaction);

		try {
			if (handleRequest(transaction, request, context) == false) {
				response.setStatusCode(503);
				transaction.signalComplete();
				return;
			}
			final boolean isSSL = isSslConnection(context);
			HttpUriRequest uriRequest = uriRequestCreator.createUriRequest(transaction.getRequest(), isSSL);
			BasicHttpContext ctx = new BasicHttpContext();
			IHttpResponse r = requestEngine.sendRequest(uriRequest, ctx);
			if(r == null) {
				response.setStatusCode(503);
				transaction.signalComplete();
				return;
			}
			transaction.setHttpHost((HttpHost) ctx.getAttribute(ExecutionContext.HTTP_TARGET_HOST));

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

	private boolean isSslConnection(HttpContext context) throws HttpException {
		final HttpServerConnection conn = (HttpServerConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
		if(!(conn instanceof VegaHttpServerConnection))
			throw new HttpException("HttpServerConnection is not expected type "+ conn);
		return ((VegaHttpServerConnection) conn).isSslConnection();
	}

	private HttpRequest copyRequest(HttpRequest request) {
		if(request instanceof HttpEntityEnclosingRequest)
			return copyEntityEnclosingRequest((HttpEntityEnclosingRequest) request);
		else
			return copyBasicRequest(request);
	}

	private HttpRequest copyEntityEnclosingRequest(HttpEntityEnclosingRequest request) {
		final HttpEntity e = copyEntity(request.getEntity());
		final BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest(request.getRequestLine());
		r.setEntity(e);
		copyHeaders(request, r);
		return r;
	}

	private HttpRequest copyBasicRequest(HttpRequest request) {
		if(request == null)
			return null;
		final HttpRequest r = new BasicHttpRequest(request.getRequestLine());
		copyHeaders(request, r);
		return r;
	}

	private static void copyHeaders(HttpMessage from, HttpMessage to) {
		for(Header h: from.getAllHeaders())
			to.addHeader(new BasicHeader(h.getName(), h.getValue()));
	}

	private HttpEntity copyEntity(HttpEntity entity) {
		try {
			if(entity == null)
				return null;
			final ByteArrayEntity newEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity));
			newEntity.setContentEncoding(entity.getContentEncoding());
			newEntity.setContentType(entity.getContentType());
			return newEntity;
		} catch (IOException e) {
			return null;
		}
	}

	private HttpResponse copyResponse(HttpResponse originalResponse) {
		HttpResponse r = new BasicHttpResponse(originalResponse.getStatusLine());
		r.setHeaders(originalResponse.getAllHeaders());
		r.setEntity(originalResponse.getEntity());
		return r;
	}

	private void removeHopByHopHeaders(HttpMessage message) {
		for(String hdr: HOP_BY_HOP_HEADERS) 
			message.removeHeaders(hdr);
	}

	private boolean handleRequest(ProxyTransaction transaction, HttpRequest request, HttpContext context) throws InterruptedException {
		removeHopByHopHeaders(request);
		transaction.setRequest(copyRequest(request));
		
		if (httpProxy.handleTransaction(transaction) == true) {
			return transaction.getForward();
		} else {
			return true;
		}
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
