package com.subgraph.vega.internal.crawler;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.subgraph.vega.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.urls.IUrlExtractor;

public class WebCrawler implements IWebCrawler {
	private final static int CRAWLER_THREAD_COUNT = 5;
	private final IWebModel model;
	private final IUrlExtractor urlExtractor;
	private final IHttpRequestEngine requestEngine;
	private final URI baseURI;
	private final UrlFilter filter;
	private final Executor executor = Executors.newFixedThreadPool(CRAWLER_THREAD_COUNT + 1);
	private final BlockingQueue<CrawlerTask> requestQueue = new LinkedBlockingQueue<CrawlerTask>();
	private final BlockingQueue<CrawlerTask> responseQueue = new LinkedBlockingQueue<CrawlerTask>();
	
	WebCrawler(IWebModel model, IUrlExtractor urlExtractor, IHttpRequestEngine requestEngine, URI baseURI) {
		this.model = model;
		this.urlExtractor = urlExtractor;
		this.requestEngine = requestEngine;
		this.baseURI = baseURI;
		this.filter = new UrlFilter(baseURI);
		
	}
	@Override
	public void start() {
		IWebHost host = model.getWebHostByNameAndPort(baseURI.getHost(), baseURI.getPort());
		if(host == null) {
			// XXX
			return;
		}
		IWebPath path = host.addPath(baseURI.getPath());
		
		for(IWebPath wp: path.getUnvisitedPaths()) {
			URI uri = wp.toURI();
			if(filter.filter(uri))
				requestQueue.add(CrawlerTask.createGetTask(uri));
		}
			
		executor.execute( new HttpResponseProcessor(requestQueue, responseQueue, model, urlExtractor, filter));
		
		for(int i = 0; i < CRAWLER_THREAD_COUNT; i++)
			executor.execute(new RequestConsumer(requestEngine, requestQueue, responseQueue));
		
	}

}
