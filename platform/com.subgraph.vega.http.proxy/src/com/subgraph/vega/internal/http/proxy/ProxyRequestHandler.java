package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class ProxyRequestHandler implements HttpRequestHandler {

	private final static String[] HOP_BY_HOP_HEADERS = {
		HTTP.CONTENT_LEN, HTTP.TRANSFER_ENCODING, HTTP.CONN_DIRECTIVE, 
		"Keep-Alive", "Proxy-Authenticate", "TE", "Trailers", "Upgrade"
	};

	private final IHttpRequestEngine requestEngine;

	ProxyRequestHandler(IHttpRequestEngine requestEngine) {
		this.requestEngine = requestEngine;
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {

		context.setAttribute(HttpProxy.PROXY_CONTEXT_REQUEST, copyRequest(request));

		removeHopByHopHeaders(request);
		request.removeHeaders("Host");
		final HttpUriRequest uriRequest = requestToUriRequest(request);
		if(uriRequest == null)
			return;

		BasicHttpContext ctx = new BasicHttpContext();
		IHttpResponse r = requestEngine.sendRequest(uriRequest, ctx);
		HttpResponse httpResponse = r.getRawResponse();
		context.setAttribute(HttpProxy.PROXY_CONTEXT_RESPONSE, copyResponse(httpResponse));
		context.setAttribute(HttpProxy.PROXY_HTTP_HOST, ctx.getAttribute(ExecutionContext.HTTP_TARGET_HOST));

		if(httpResponse == null)
			return;

		removeHopByHopHeaders(httpResponse);

		response.setStatusLine(httpResponse.getStatusLine());
		response.setHeaders(httpResponse.getAllHeaders());
		response.setEntity(httpResponse.getEntity());
	}

	private HttpRequest copyRequest(HttpRequest originalRequest) {
		HttpRequest r = new BasicHttpRequest(originalRequest.getRequestLine());
		r.setHeaders(originalRequest.getAllHeaders());
		return r;
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

	private HttpUriRequest requestToUriRequest(HttpRequest request) {
		final String uriLine = request.getRequestLine().getUri();

		final URI uri = stringToURI(uriLine);
		final String method = request.getRequestLine().getMethod();
		final HttpUriRequest uriRequest =  methodStringToUriRequest(method, uri);
		if(uriRequest == null)
			return null;

		uriRequest.setParams(request.getParams());
		uriRequest.setHeaders(request.getAllHeaders());
		return uriRequest;
	}

	private URI stringToURI(String uriString) {
		try {
			return new URI(uriString);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Could  not parse URI string "+ uriString, e);
		}
	}

	private HttpUriRequest methodStringToUriRequest(String methodString, URI uri) {
		final String m = methodString.toUpperCase();
		if(m.equals("GET"))
			return new HttpGet(uri);
		else if(m.equals("POST"))
			return new HttpPost(uri);
		else if(m.equals("HEAD"))
			return new HttpHead(uri);
		else if(m.equals("PUT"))
			return new HttpPut(uri);
		else if(m.equals("DELETE"))
			return new HttpDelete(uri);
		else if(m.equals("OPTIONS"))
			return new HttpOptions(uri);
		else if(m.equals("TRACE"))
			return new HttpTrace(uri);
		else 
			throw new IllegalArgumentException("Illegal HTTP method name "+ methodString);
	}
}
