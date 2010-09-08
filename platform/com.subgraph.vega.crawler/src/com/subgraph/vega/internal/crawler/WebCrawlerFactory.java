package com.subgraph.vega.internal.crawler;

import java.net.URI;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.crawler.IWebCrawler;
import com.subgraph.vega.crawler.IWebCrawlerFactory;
import com.subgraph.vega.urls.IUrlExtractor;

public class WebCrawlerFactory implements IWebCrawlerFactory {

	private IModel model;
	private IUrlExtractor urlExtractor;
	
	@Override
	public IWebCrawler create(URI baseURI) {
		final IWebModel webModel = model.getCurrentWorkspace().getWebModel();
		return new WebCrawler(webModel, urlExtractor, baseURI);
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

}
