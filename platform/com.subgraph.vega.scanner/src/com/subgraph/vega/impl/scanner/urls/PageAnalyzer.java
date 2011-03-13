package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerResult;
import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.state.PathState;

public class PageAnalyzer {
	private final UriFilter uriFilter;
	private final IContentAnalyzer contentAnalyzer;
	private final UriParser uriParser;
	private boolean isFormParsingEnabled = false;
	
	public PageAnalyzer(UriFilter uriFilter, IContentAnalyzer contentAnalyzer, UriParser uriParser) {
		this.uriFilter = uriFilter;
		this.contentAnalyzer = contentAnalyzer;
		this.uriParser = uriParser;
	}
	
	public void analyzePage(HttpUriRequest request, IHttpResponse response, PathState pathState) {
		IContentAnalyzerResult result = contentAnalyzer.processResponse(response, false);
		filterAndQueueURIs(result.getDiscoveredURIs());
		if(isFormParsingEnabled) {
			final IHTMLParseResult html = response.getParsedHTML();
			if(html != null) {
				//webModel.parseForms(path, html.getDOMDocument());
			}
		}
	}
	
	private void filterAndQueueURIs(List<URI> uris) {
		for(URI u: uris) {
			if(uriFilter.filter(u)) {
				uriParser.processUri(u);
			}
		}
	}
}
