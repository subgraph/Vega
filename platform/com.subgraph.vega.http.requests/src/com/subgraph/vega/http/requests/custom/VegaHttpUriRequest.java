package com.subgraph.vega.http.requests.custom;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicRequestLine;

import com.subgraph.vega.api.util.UriTools;
import com.subgraph.vega.internal.http.requests.config.IRequestEncodingStrategy;

public class VegaHttpUriRequest extends HttpRequestBase implements IEncodableHttpRequest {
	
	public static VegaHttpUriRequest createFrom(HttpHost host, HttpRequest request) {
		final VegaHttpUriRequest newRequest = new VegaHttpUriRequest(host, request.getRequestLine());
		newRequest.setHeaders(request.getAllHeaders());
		return newRequest;
	}

	private final HttpHost targetHost;
	private final RequestLine requestLine;
	private RequestLine encodedRequestLine;

	public VegaHttpUriRequest(String methodName, URI uri) {
		this(URIUtils.extractHost(uri), methodName, uriToRequestUri(uri));
	}
	
	private static String uriToRequestUri(URI uri) {
		final StringBuilder sb = new StringBuilder();
		if(uri.getPath() != null) {
			sb.append(uri.getPath());
		}
		
		if(uri.getQuery() != null) {
			if(sb.length() == 0) {
				sb.append("/");
			}
			sb.append('?');
			sb.append(uri.getQuery());
		}
		return sb.toString();
	}
	
	private static RequestLine createRequestLine(String method, String requestUri) {
		if(requestUri == null || requestUri.isEmpty()) {
			return new BasicRequestLine(method, "/", HttpVersion.HTTP_1_1);
		} else {
			return new BasicRequestLine(method, requestUri, HttpVersion.HTTP_1_1);
		}
	}

	public VegaHttpUriRequest(HttpHost targetHost, String methodName, String requestUri) {
		this(targetHost, createRequestLine(methodName, requestUri));
	}

	public VegaHttpUriRequest(HttpHost targetHost, RequestLine requestLine) {
		this.targetHost = targetHost;
		this.requestLine = requestLine;
		setURI(UriTools.createUriFromTargetAndLine(targetHost, requestLine.getUri()));
	}
	
	public HttpHost getTargetHost() {
		return targetHost;
	}

	public String getScheme() {
		return targetHost.getSchemeName();
	}

	@Override
	public RequestLine getRequestLine() {
		if(encodedRequestLine != null) {
			return encodedRequestLine;
		} else {
			return requestLine;
		}
	}

	@Override
	public String getMethod() {
		return requestLine.getMethod();
	}

	@Override
	public void encodeWith(IRequestEncodingStrategy encodingStrategy) {
		encodedRequestLine = encodingStrategy.encodeRequestLine(requestLine);
	}
}
