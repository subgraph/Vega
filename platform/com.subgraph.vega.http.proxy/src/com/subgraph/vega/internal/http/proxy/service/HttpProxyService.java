package com.subgraph.vega.internal.http.proxy.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ByteArrayEntity;

import com.subgraph.vega.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.http.proxy.IHttpProxyService;
import com.subgraph.vega.http.proxy.IProxyTransaction;
import com.subgraph.vega.internal.http.proxy.HttpInterceptProxy;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.requestlog.IRequestLog;
import com.subgraph.vega.urls.IUrlExtractor;

public class HttpProxyService implements IHttpProxyService {
	
	private final Logger logger = Logger.getLogger(HttpProxyService.class.getName());
	private IModel model;
	private IWebModel webModel;
	private IRequestLog requestLog;
	private IUrlExtractor urlExtractor;
	private HttpInterceptProxy proxy;

	@Override
	public void start() {
		if(model == null)
			logger.warning("Model service is NULL");
		if(urlExtractor == null)
			logger.warning("URL extraction service is NULL");
		
		webModel = model.getCurrentWorkspace().getWebModel();
		requestLog = model.getCurrentWorkspace().getRequestLog();
		proxy = new HttpInterceptProxy(8888);
		proxy.registerEventHandler(new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				processTransaction(transaction);
			}
		});
		proxy.startProxy();
	}
	
	private void processTransaction(IProxyTransaction transaction) {
		if(model == null)
			return;
		HttpEntity responseEntity = transactionToResponseEntity(transaction);
		if(responseEntity == null)
			return;
		
		requestLog.addRequestResponse(transaction.getRequest(), transaction.getResponse(), transaction.getHttpHost(), transaction.getTargetAddress());
		
		String hostname = transactionToHostname(transaction);
		if(hostname == null)
			return;
		String mimeType = transactionToMimeType(transaction);
		String urlPath = transaction.getRequest().getRequestLine().getUri();
		try {
			URI uri = new URI(urlPath);
			addGetTargetToModel(transaction.getHttpHost(), transaction.getTargetAddress(), uri, mimeType);
			if(responseEntity != null)
				addDiscoveredLinks(hostname, urlPath, responseEntity);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private String transactionToMimeType(IProxyTransaction transaction) {
		if(transaction.getResponse().getFirstHeader("Content-Type") == null)
			return "unknown/unknown";
		else
			return transaction.getResponse().getFirstHeader("Content-Type").getValue();
	}
	
	private HttpEntity transactionToResponseEntity(IProxyTransaction transaction) {
		HttpEntity entity = transaction.getResponse().getEntity();
		if(entity == null)
			return null;
		if(entity.getContentEncoding() == null)
			return entity;
		if(entity.getContentEncoding().getValue().equals("gzip"))
			try {
				return gzipDecompress(entity);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		return null;
	}
	
	private HttpEntity gzipDecompress(HttpEntity entity) throws IOException {
		final byte[] buffer = new byte[1024];
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final GZIPInputStream gzip = new GZIPInputStream(entity.getContent());
		while(gzip.available() != 0) {
			int n = gzip.read(buffer, 0, buffer.length);
			if(n != -1)
				out.write(buffer, 0, n);
		}
		ByteArrayEntity e = new ByteArrayEntity(out.toByteArray());
		e.setContentEncoding(entity.getContentEncoding());
		e.setContentType(entity.getContentType());
		return e;
	}
	
	private String transactionToHostname(IProxyTransaction transaction) {
		Header hostHeader = transaction.getRequest().getFirstHeader("Host");
		if(hostHeader == null || hostHeader.getValue() == null)
			return null;
		else
			return hostHeader.getValue();
	}
	
	private void addGetTargetToModel(HttpHost httpHost, InetAddress address, URI uri, String mimeType) {
		IWebHost hostEntity = webModel.addWebHost(httpHost.getHostName(), address, httpHost.getPort(), httpHost.getSchemeName().equals("https"));
		IWebPath pathEntity = hostEntity.addPath(uri.getPath());
		IWebGetTarget getTarget = pathEntity.addGetTarget(uri.getQuery(), mimeType);
		getTarget.setVisited(true);
	}
	
	private void addDiscoveredLinks(String hostname, String urlPath, HttpEntity entity) {
		if(urlExtractor == null)
			return;
		try {
			final URI uri = new URI(urlPath);
			final URI base = new URI("http", hostname, uri.getPath(), null);
			for(URI u: urlExtractor.findUrls(entity, base))
				webModel.addURI(u);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
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

}
