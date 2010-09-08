package com.subgraph.vega.internal.crawler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.urls.IUrlExtractor;

public class HttpResponseProcessor implements Runnable {
	private final BlockingQueue<CrawlerTask> crawlerRequestQueue;
	private final BlockingQueue<CrawlerTask> crawlerResponseQueue;
	private final IWebModel model;
	private final IUrlExtractor extractor;
	private final UrlFilter filter;
	
	HttpResponseProcessor(BlockingQueue<CrawlerTask> requestQueue, BlockingQueue<CrawlerTask> responseQueue, IWebModel model, IUrlExtractor extractor, UrlFilter filter) {
		this.crawlerRequestQueue = requestQueue;
		this.crawlerResponseQueue = responseQueue;
		this.model = model;
		this.extractor = extractor;
		this.filter = filter;
	}

	@Override
	public void run() {
		try {
			runLoop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void runLoop() throws InterruptedException {
		while(true) {
				
			CrawlerTask task = crawlerResponseQueue.take();
			if(task.isExitTask()) {
				crawlerRequestQueue.add(CrawlerTask.createExitTask());
				return;
			}
			
			try {
				processResponse(task.getResponse(), task.getRequest().getURI());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(task.finishTask()) {
				crawlerRequestQueue.add(CrawlerTask.createExitTask());
				return;
			}
			
		}
	}
	
	private void processResponse(HttpResponse response, URI page) throws IOException {
		HttpEntity entity = response.getEntity();
		if(entity == null)
			return;
		processEntity(entity, page);
		entity.consumeContent();
	}
	
	private void processEntity(HttpEntity entity, URI page) {
		Header contentType = entity.getContentType();
		String mimeType = (contentType == null) ? (null) : (contentType.getValue());
		IWebPath path = model.addURI(page);
		IWebGetTarget getTarget = path.addGetTarget(page.getQuery(), mimeType);
		getTarget.setVisited(true);
		if(mimeType != null && mimeType.startsWith("text/html")) {
			List<URI> uris = extractor.findUrls(entity, page);
			filterAndQueueURIs(uris);
		}
	}
	
	private void filterAndQueueURIs(List<URI> uris) {
		for(URI u: uris) {
			if(filter.filter(u)) 
				crawlerRequestQueue.add(CrawlerTask.createGetTask(u));
		}
	}
}
