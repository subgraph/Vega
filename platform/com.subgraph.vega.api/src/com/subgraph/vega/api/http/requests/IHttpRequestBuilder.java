package com.subgraph.vega.api.http.requests;

import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public interface IHttpRequestBuilder {
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

	public void setHost(String host);
	public String getHost();
	public void setHostPort(int port);
	public int getHostPort();
	
	public void setMethod(String method);
	public String getMethod();
	public void setPath(String path);
	public String getPath();
	public String getRequestLine();

	public IHttpHeaderBuilder addHeader(String name, String value);
	public void removeHeader(IHttpHeaderBuilder header);
	public void swapHeader(int idx1, int idx2);
	public int getHeaderIdxOf(IHttpHeaderBuilder next);
	public int getHeaderCnt();
	public IHttpHeaderBuilder getHeader(int idx);
	public IHttpHeaderBuilder[] getHeaders();

	public HttpUriRequest buildRequest() throws URISyntaxException;
}
