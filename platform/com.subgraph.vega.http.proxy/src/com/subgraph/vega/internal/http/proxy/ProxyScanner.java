package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.IProxyScan;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.util.VegaURI;

public class ProxyScanner {
	private final Logger logger = Logger.getLogger(HttpProxyService.class.getName());
	
	private final IScanner scanner;
	private final CookieStore cookieStore;
	private final ProxyScannerScopeTracker scopeTracker;
	
	
	private IProxyScan proxyScan;
	private boolean isEnabled = false;
	
	ProxyScanner(IScanner scanner, CookieStore cookieStore, IModel model) {
		this.scanner = scanner;
		this.cookieStore = cookieStore;
		this.scopeTracker = new ProxyScannerScopeTracker(model, this);
	}
	
	void reloadModules() {
		if(proxyScan != null) {
			proxyScan.reloadModules();
		}
	}

	boolean isEnabled() {
		return isEnabled;
	}
	
	void setEnabled(boolean value) {
		reloadModules();
		isEnabled = value;
	}
	
	List<IBasicModuleScript> getInjectionModules() {
		if(proxyScan == null) {
			return Collections.emptyList();
		}
		return proxyScan.getInjectionModules();
	}

	IScannerConfig getConfig() {
		if(proxyScan == null) {
			return null;
		}
		return proxyScan.getConfig();
	}
	
	void processRequest(HttpUriRequest request) {
		if(proxyScan == null) {
			throw new IllegalStateException("Cannot process request because no proxy scan is currently active");
		}
		if(request.getMethod().equalsIgnoreCase("GET")) {
			handleProxyScanGetRequest(request);
		} else if(request.getMethod().equalsIgnoreCase("POST")) {
			handleProxyScanPostRequest(request);
		}
	}
	
	
	void handleWorkspaceChanged(IWorkspace newWorkspace) {
		if(proxyScan != null) {
			proxyScan.stop();
		}
		isEnabled = false;
		if(newWorkspace != null) {
			proxyScan = scanner.createProxyScan(newWorkspace, cookieStore);
		}
	}
	
	
	private void handleProxyScanGetRequest(HttpUriRequest request) {
		
		final List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
		if(params.isEmpty()) {
			return;
		}
		final URI target = request.getURI();
		if(isTargetInScope(target)) {
			proxyScan.scanGetTarget(requestToURI(request), params);
		}
	}

	private void handleProxyScanPostRequest(HttpUriRequest request) {
		if(!(request instanceof HttpEntityEnclosingRequest)) {
			return;
		}
		final HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
		List<NameValuePair> params;
		try {
			params = URLEncodedUtils.parse(entity);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception reading request entity", e);
			return;
		}
		if(params.isEmpty()) {
			return;
		}
		final URI target = request.getURI();
		if(isTargetInScope(target)) {
			proxyScan.scanPostTarget(requestToURI(request), params);
		}
	}

	private VegaURI requestToURI(HttpUriRequest request) {
		final URI u = request.getURI();
		final HttpHost targetHost = URIUtils.extractHost(u);
		return new VegaURI(targetHost, u.getPath(), u.getQuery());
	}

	private boolean isTargetInScope(URI target) {
		final ITargetScope activeScope = scopeTracker.getCurrentActiveScope();
		if(activeScope == null) {
			return false;
		}
		return activeScope.filter(target);
	}
}
