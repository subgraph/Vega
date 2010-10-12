package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.jsoup.nodes.Document;

public interface IHttpResponse {
	HttpRequest getOriginalRequest();
	HttpResponse getRawResponse();
	String getBodyAsString();
	Document getHtml();
}
