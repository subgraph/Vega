package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.impl.scanner.state.PathState;
import com.subgraph.vega.urls.IUrlExtractor;

public class PageAnalyzer {
	private final Logger logger = Logger.getLogger("scanner");
	private final IWebModel webModel;
	private final UriFilter uriFilter;
	private final IUrlExtractor urlExtractor;
	private final UriParser uriParser;
	private boolean isFormParsingEnabled = false;
	
	public PageAnalyzer(IWebModel webModel, UriFilter uriFilter, IUrlExtractor urlExtractor, UriParser uriParser) {
		this.webModel = webModel;
		this.uriFilter = uriFilter;
		this.urlExtractor = urlExtractor;
		this.uriParser = uriParser;
	}
	
	public void analyzePage(HttpUriRequest request, IHttpResponse response, PathState pathState) {
		processEntity(request, response);
	}
	
	
	private void processEntity(HttpUriRequest request, IHttpResponse response) {
		if(response == null)
			logger.warning("No response in PageAnalyzer.processEntity() for request "+ request.getURI());
			
		final HttpEntity entity = response.getRawResponse().getEntity();
		if(entity == null)
			return;
		final Header contentType = entity.getContentType();
		final String mimeType = (contentType == null) ? (null) : (contentType.getValue());
		
		if(mimeType != null && mimeType.contains("html")) {
			List<URI> uris = urlExtractor.findUrls(response);
			
			filterAndQueueURIs(uris);
			if(isFormParsingEnabled) {
				final IHTMLParseResult html = response.getParsedHTML();
				if(html != null) {
					//webModel.parseForms(path, html.getDOMDocument());
				}
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
