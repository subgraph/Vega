package com.subgraph.vega.internal.crawler;

import java.net.URI;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.urls.IUrlExtractor;

public class WebCrawlerFactory implements IWebCrawlerFactory {

	private IModel model;
	private IUrlExtractor urlExtractor;
	private IHttpRequestEngine requestEngine;
	
	@Override
	public IWebCrawler create(ICrawlerConfig config) {
		final IWebModel webModel = model.getCurrentWorkspace().getWebModel();
		return new WebCrawler(webModel, urlExtractor, requestEngine, config);
	}
	
	public ICrawlerConfig createBasicConfig(URI baseURI) {
		return new BasicCrawlerConfig(baseURI);
	}
	
	protected void setModel(IModel model) {
		this.model = model;
	}
	
	protected void unsetModel(IModel model) {
		this.model = null;
	}
	
	protected void setUrlExtractor(IUrlExtractor extractor) {
		this.urlExtractor = extractor;
	}
	
	protected void unsetUrlExtractor(IUrlExtractor extractor) {
		this.urlExtractor = null;
	}
	
	protected void setRequestEngine(IHttpRequestEngine requestEngine) {
		this.requestEngine = requestEngine;
	}
	
	protected void unsetRequestEngine(IHttpRequestEngine requestEngine) {
		this.requestEngine = null;
	}

}
