package com.subgraph.vega.api.http.requests;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public  interface IHttpRequestBuilder extends IHttpMessageBuilder {
	/**
	 * Set request fields from a HttpRequest provided by IRequestLogRecord. Any previously set fields are unset.
	 *
	 * @param request IRequestLogRecord containing HttpRequest.
	 * @throws URISyntaxException 
	 */
	void setFromRequest(IRequestLogRecord record) throws URISyntaxException;

	/**
	 * Set request fields from a HttpRequest. Any previously set fields are unset.
	 *
	 * @param request HttpRequest
	 * @throws URISyntaxException 
	 */
	void setFromRequest(HttpRequest request) throws URISyntaxException;

	/**
	 * Set the request method, scheme, host, host port, path, and protocol version fields from a RequestLine.
	 *
	 * @param requestLine RequestLine 
	 * @throws URISyntaxException 
	 */
	void setFromRequestLine(RequestLine requestLine) throws URISyntaxException;

	/**
	 * Set the scheme, host, host port, and path from a URI.
	 * 
	 * @param uri URI
	 */
	void setFromUri(URI uri);

	/**
	 * Set the scheme, host, and host port from a HttpHost.
	 *
	 * @param host HttpHost
	 */
	void setFromHttpHost(HttpHost host);
	
	void setScheme(String scheme);
	String getScheme();

	void setHost(String host);
	String getHost();
	void setHostPort(int port);
	int getHostPort();
	
	void setMethod(String method);
	String getMethod();

	void setPath(String path);
	String getPath();

	void setRawRequestLine(String line);
	String getRawRequestLine();
	String getRequestLine();

	HttpUriRequest buildRequest() throws URISyntaxException;
}
