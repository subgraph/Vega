package com.subgraph.vega.internal.analysis.urls;

import java.net.URI;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public class UrlExtractor {
	private final HtmlUrlExtractor htmlExtractor = new HtmlUrlExtractor();

	public List<URI> findUrls(IHttpResponse response) {
		return htmlExtractor.findHtmlUrls(response);
	}
}
