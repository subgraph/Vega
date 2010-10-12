package com.subgraph.vega.internal.http.proxy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpResponse;
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
	private IHttpRequestEngineFactory requestEngineFactory;
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
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		proxy = new HttpProxy(proxyPort, requestEngine);
		proxy.registerEventHandler(eventHandler);
		proxy.startProxy();
	}

	private void processTransaction(IProxyTransaction transaction) {

		requestLog.addRequestResponse(transaction.getRequest(), transaction.getResponse().getRawResponse(), transaction.getHttpHost());

		HttpEntity responseEntity = transaction.getResponse().getRawResponse().getEntity();
		if(responseEntity == null)
			return;

		final String mimeType = transactionToMimeType(transaction);
		final URI uri = transactionToURI(transaction);
		if(uri == null)
			return;

		addGetTargetToModel(transaction.getHttpHost(), uri, mimeType);
		addDiscoveredLinks(transaction.getResponse());
	}

	private String transactionToMimeType(IProxyTransaction transaction) {
		HttpResponse response = transaction.getResponse().getRawResponse();
		Header hdr = response.getFirstHeader("Content-Type");
		if(hdr == null || hdr.getValue() == null)
			return "unknown/unknown";
		else
			return hdr.getValue();
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
	
	private void addDiscoveredLinks(IHttpResponse response) {
		for(URI u: urlExtractor.findUrls(response))
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

	protected void setRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = factory;
	}

	protected void unsetRequestEngineFactory(IHttpRequestEngineFactory factory) {
		this.requestEngineFactory = null;
	}
}
