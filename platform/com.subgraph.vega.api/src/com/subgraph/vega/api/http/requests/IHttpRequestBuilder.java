package com.subgraph.vega.api.http.requests;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpParams;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public interface IHttpRequestBuilder {
	/**
	 * Set request parameters. A copy of the parameters is not made.
	 *
	 * @param params Request parameters.
	 */
	public void setParams(HttpParams params);
	public HttpParams getParams();
	
	/**
	 * Set request fields from a HttpRequest provided by IRequestLogRecord. Any previously set fields are unset.
	 *
	 * @param request IRequestLogRecord containing HttpRequest.
	 * @throws URISyntaxException 
	 */
	public void setFromRequest(IRequestLogRecord request) throws URISyntaxException;

	/**
	 * Set request fields from a HttpRequest. Any previously set fields are unset.
	 *
	 * @param reqest HttpRequest.
	 * @throws URISyntaxException 
	 */
	public void setFromRequest(HttpRequest request) throws URISyntaxException;

	/**
	 * Set the request method, path, and protocol version fields from a RequestLine.
	 *
	 * @param requestLine RequestLine 
	 * @throws URISyntaxException 
	 */
	public void setFromRequestLine(RequestLine requestLine) throws URISyntaxException;
	
	public void setRawRequestLine(String line);
	public String getRawRequestLine();

	/**
	 * Set the path from a URI. The host and port are set if they are set in the URI.
	 * 
	 * @param uri URI
	 */
	public void setFromUri(URI uri);
	public void setFromHttpHost(HttpHost host);
	
	public void setScheme(String scheme);
	public String getScheme();

	public void setHost(String host);
	public String getHost();
	public void setHostPort(int port);
	public int getHostPort();
	
	public void setMethod(String method);
	public String getMethod();
	public void setPath(String path);
	public String getPath();
	void setProtocolVersion(ProtocolVersion protocolVersion);
	ProtocolVersion getProtocolVersion();
	public String getRequestLine();

	public IHttpHeaderBuilder addHeader(String name, String value);
	public void removeHeader(IHttpHeaderBuilder header);
	public void swapHeader(int idx1, int idx2);
	public int getHeaderIdxOf(IHttpHeaderBuilder next);
	public int getHeaderCnt();
	public IHttpHeaderBuilder getHeader(int idx);
	public IHttpHeaderBuilder[] getHeaders();

	public void setEntity(HttpEntity entity);
	public HttpEntity getEntity();
	
	public HttpUriRequest buildRequest() throws URISyntaxException;
}
