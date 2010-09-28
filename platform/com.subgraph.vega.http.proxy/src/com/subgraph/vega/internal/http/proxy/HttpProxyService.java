package com.subgraph.vega.internal.http.proxy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;

import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.requestlog.IRequestLog;
import com.subgraph.vega.urls.IUrlExtractor;

public class HttpProxyService implements IHttpProxyService {

	private final Logger logger = Logger.getLogger(HttpProxyService.class.getName());
	private final IHttpInterceptProxyEventHandler eventHandler;
	private IModel model;
	private IWebModel webModel;
	private IRequestLog requestLog;
	private IHttpRequestEngine requestEngine;
	private IUrlExtractor urlExtractor;
	private HttpProxy proxy;

	public HttpProxyService() {
		eventHandler = new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				processTransaction(transaction);
			}
		};
	}

	@Override
	public void start(int proxyPort) {
		webModel = model.getCurrentWorkspace().getWebModel();
		requestLog = model.getCurrentWorkspace().getRequestLog();
	
		proxy = new HttpProxy(proxyPort, requestEngine);
		proxy.registerEventHandler(eventHandler);
		proxy.startProxy();
	}

	private void processTransaction(IProxyTransaction transaction) {

		requestLog.addRequestResponse(transaction.getRequest(), transaction.getResponse(), transaction.getHttpHost());

		HttpEntity responseEntity = transaction.getResponse().getEntity();
		if(responseEntity == null)
			return;

		final String mimeType = transactionToMimeType(transaction);
		final URI uri = transactionToURI(transaction);
		if(uri == null)
			return;

		addGetTargetToModel(transaction.getHttpHost(), uri, mimeType);
		addDiscoveredLinks(transaction.getHttpHost(), uri, responseEntity);
	}

	private String transactionToMimeType(IProxyTransaction transaction) {
		if(transaction.getResponse().getFirstHeader("Content-Type") == null)
			return "unknown/unknown";
		else
			return transaction.getResponse().getFirstHeader("Content-Type").getValue();
	}

	private URI transactionToURI(IProxyTransaction transaction) {
		String urlPath = transaction.getRequest().getRequestLine().getUri();
		try {
			return new URI(urlPath);
		} catch (URISyntaxException e) {
			logger.warning("Failed to parse URL from HTTP request: "+ urlPath);
			return null;
		}
	}

	private void addGetTargetToModel(HttpHost httpHost, URI uri, String mimeType) {
		IWebHost hostEntity = webModel.addWebHost(httpHost.getHostName(), httpHost.getPort(), httpHost.getSchemeName().equals("https"));
		IWebPath pathEntity = hostEntity.addPath(uri.getPath());
		IWebGetTarget getTarget = pathEntity.addGetTarget(uri.getQuery(), mimeType);
		getTarget.setVisited(true);
	}
	
	private void addDiscoveredLinks(HttpHost httpHost, URI uri, HttpEntity entity) {
		final URI hostURI = URI.create(httpHost.toURI());
		final URI base = hostURI.resolve(uri);
		for(URI u: urlExtractor.findUrls(entity, base))
			webModel.addURI(u);
	}

	@Override
	public void stop() {
		proxy.unregisterEventHandler(eventHandler);
		proxy.stopProxy();		
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

	protected void setRequestEngine(IHttpRequestEngine engine) {
		this.requestEngine = engine;
	}

	protected void unsetRequestEngine(IHttpRequestEngine engine) {
		this.requestEngine = null;
	}
}
