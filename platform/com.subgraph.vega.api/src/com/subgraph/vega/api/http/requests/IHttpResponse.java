package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.html.IHTMLParseResult;

public interface IHttpResponse {
	HttpRequest getOriginalRequest();
	HttpResponse getRawResponse();
	String getBodyAsString();
	IHTMLParseResult getParsedHTML();
}
