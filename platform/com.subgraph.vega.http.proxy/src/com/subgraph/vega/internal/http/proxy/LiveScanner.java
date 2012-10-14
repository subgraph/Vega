package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.ILiveScan;
import com.subgraph.vega.api.scanner.IScanner;

public class LiveScanner {
	private final Logger logger = Logger.getLogger(HttpProxyService.class.getName());
	
	private final IScanner scanner;
	private final LiveScannerScopeTracker scopeTracker;
	
	
	private ILiveScan liveScan;
	private boolean isEnabled = false;
	
	LiveScanner(IScanner scanner, IModel model) {
		this.scanner = scanner;
		this.scopeTracker = new LiveScannerScopeTracker(model, this);
	}
	
	boolean isEnabled() {
		return isEnabled;
	}
	
	void setEnabled(boolean value) {
		System.out.println("Set livescanner enabled: "+ value);
		isEnabled = value;
	}
	
	void processRequest(HttpUriRequest request) {
		System.out.println("Process request: "+ request);
		if(liveScan == null) {
			throw new IllegalStateException("Cannot process request because no live scan is currently active");
		}
		if(request.getMethod().equalsIgnoreCase("GET")) {
			handleLiveScanGetRequest(request);
		} else if(request.getMethod().equalsIgnoreCase("POST")) {
			handleLiveScanPostRequest(request);
		}
	}
	
	
	void handleWorkspaceChanged(IWorkspace newWorkspace) {
		if(liveScan != null) {
			liveScan.stop();
		}
		System.out.println("creating live scan");
		liveScan = scanner.createLiveScan(newWorkspace);
	}
	
	
	private void handleLiveScanGetRequest(HttpUriRequest request) {
		
		final List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
		if(params.isEmpty()) {
			return;
		}
		final URI target = request.getURI();
		if(isTargetInScope(target)) {
			liveScan.scanGetTarget(target, params);
		}
	}
	
	private void handleLiveScanPostRequest(HttpUriRequest request) {
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
			liveScan.scanPostTarget(target, params);
		}
	}

	private boolean isTargetInScope(URI target) {
		final ITargetScope activeScope = scopeTracker.getCurrentActiveScope();
		if(activeScope == null) {
			return false;
		}
		return activeScope.filter(target);
	}
}
