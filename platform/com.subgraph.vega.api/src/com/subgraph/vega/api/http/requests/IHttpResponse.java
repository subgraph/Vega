package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.html.IHTMLParseResult;

public interface IHttpResponse {
	HttpRequest getOriginalRequest();
	HttpResponse getRawResponse();
	HttpHost getHost();
	String getBodyAsString();
	IHTMLParseResult getParsedHTML();
	void logResponse();
}
