package com.subgraph.vega.internal.http.proxy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.urls.IUrlExtractor;

public class HttpProxyService implements IHttpProxyService {

	private final Logger logger = Logger.getLogger(HttpProxyService.class.getName());
	private final IHttpInterceptProxyEventHandler eventHandler;
	private IModel model;
	private IHttpRequestEngineFactory requestEngineFactory;
	private IUrlExtractor urlExtractor;
	private IScannerModuleRegistry moduleRepository;
	private List<IResponseProcessingModule> responseProcessingModules;
	private HttpProxy proxy;
	private IWorkspace currentWorkspace;

	public HttpProxyService() {
		eventHandler = new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				processTransaction(transaction);
			}
		};
	}
	
	public void activate() {
		currentWorkspace = model.addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent) 
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				else if(event instanceof WorkspaceCloseEvent) 
					handleWorkspaceClose((WorkspaceCloseEvent) event);				
			}
		});
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		this.currentWorkspace = event.getWorkspace();
		
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		this.currentWorkspace = null;
	}

	@Override
	public void start(int proxyPort) {
		responseProcessingModules = moduleRepository.getResponseProcessingModules();
		if(currentWorkspace == null) 
			throw new IllegalStateException("Cannot start proxy because no workspace is currently open");
		currentWorkspace.lock();
		final IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		proxy = new HttpProxy(proxyPort, requestEngine);
		proxy.registerEventHandler(eventHandler);
		proxy.startProxy();
	}

	private void processTransaction(IProxyTransaction transaction) {
		IHttpResponse response = transaction.getResponse();
		currentWorkspace.getRequestLog().addRequestResponse(response.getOriginalRequest(), response.getRawResponse(), response.getHost());
		HttpEntity responseEntity = transaction.getResponse().getRawResponse().getEntity();
		if(responseEntity == null)
			return;

		final String mimeType = transactionToMimeType(transaction);
		final URI uri = transactionToURI(transaction);
		if(uri == null)
			return;

		addGetTargetToModel(transaction.getHttpHost(), uri, mimeType);
		addDiscoveredLinks(transaction.getResponse());
		runResponseProcessingModules(transaction, mimeType);
	}

	private void runResponseProcessingModules(IProxyTransaction transaction, String mimeType) {
		for(IResponseProcessingModule module: responseProcessingModules) {
			if(module.mimeTypeFilter(mimeType) && module.responseCodeFilter(transaction.getResponse().getRawResponse().getStatusLine().getStatusCode()))
					module.processResponse(transaction.getRequest(), transaction.getResponse(), currentWorkspace);
		}
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
		IWebHost hostEntity = currentWorkspace.getWebModel().getWebHostByHttpHost(httpHost);
		IWebPath path = hostEntity.addPath(uri.getPath());
		path.addGetResponse(uri.getQuery(), mimeType);
		path.setVisited(true);
	}
	
	private void addDiscoveredLinks(IHttpResponse response) {
		for(URI u: urlExtractor.findUrls(response)) {
			currentWorkspace.getWebModel().getWebPathByUri(u);
		}
	}

	@Override
	public void stop() {
		if(currentWorkspace == null)
			throw new IllegalStateException("No workspace is open");
		proxy.unregisterEventHandler(eventHandler);
		proxy.stopProxy();		
		currentWorkspace.unlock();
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
	
	protected void setModuleRepository(IScannerModuleRegistry moduleRepository) {
		this.moduleRepository = moduleRepository;
	}
	
	protected void unsetModuleRepository(IScannerModuleRegistry moduleRepository) {
		this.moduleRepository = null;
	}
}
