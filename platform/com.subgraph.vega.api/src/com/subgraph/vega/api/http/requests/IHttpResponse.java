package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.html.IHTMLParseResult;

public interface IHttpResponse {
	enum ResponseStatus { RESPONSE_OK };
	ResponseStatus getResponseStatus();
	int getResponseCode();
	boolean isFetchFail();
	HttpRequest getOriginalRequest();
	void setRawResponse(HttpResponse response); // temporary, probably. used in interceptor.
	HttpResponse getRawResponse();
	HttpHost getHost();
	String getBodyAsString();
	IHTMLParseResult getParsedHTML();
	boolean isMostlyAscii();
	IPageFingerprint getPageFingerprint();
	void lockResponseEntity();
	long getRequestMilliseconds();
}
