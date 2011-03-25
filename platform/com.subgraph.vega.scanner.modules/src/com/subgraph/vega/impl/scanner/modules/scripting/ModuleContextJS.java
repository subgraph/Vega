package com.subgraph.vega.impl.scanner.modules.scripting;

import org.apache.http.client.methods.HttpUriRequest;
import org.mozilla.javascript.Function;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class ModuleContextJS {
	private final IModuleContext context;
	

	ModuleContextJS(IModuleContext context) {
		this.context = context;
	}

	public int getCurrentIndex() {	
		return context.getCurrentIndex();
	}

	public IPathState getPathState() {
		return context.getPathState();
	}

	public int incrementResponseCount() {
		return context.incrementResponseCount();
	}

	public void setModuleFailed() {
		context.setModuleFailed();
	}
	
	public boolean hasModuleFailed() {
		return context.hasModuleFailed();
	}

	public void addRequestResponse(HttpUriRequest request,
			IHttpResponse response) {
		context.addRequestResponse(request, response);
	}

	public void addRequestResponse(int index, HttpUriRequest request,
			IHttpResponse response) {
		context.addRequestResponse(index, request, response);
	}

	public HttpUriRequest getSavedRequest(int index) {
		return context.getSavedRequest(index);
	}

	public IHttpResponse getSavedResponse(int index) {
		return context.getSavedResponse(index);
	}

	public IPageFingerprint getSavedFingerprint(int index) {
		return context.getSavedFingerprint(index);
	}

	public void error(HttpUriRequest request, IHttpResponse response,
			String message) {
		context.error(request, response, message);
	}

	public void debug(String msg) {
		context.debug(msg);
	}

	public void analyzePage(HttpUriRequest request, IHttpResponse response) {
		context.analyzePage(request, response);
	}

	public boolean isFingerprintMatch(int idx1, int idx2) {
		return context.isFingerprintMatch(idx1, idx2);
	}

	public boolean isFingerprintMatch(int idx, IPageFingerprint fp) {
		return context.isFingerprintMatch(idx, fp);
	}

	public void submitRequest(HttpUriRequest request,
			Function callback, int index) {
		context.submitRequest(request, wrap(callback), index);
	}

	public void submitRequest(HttpUriRequest request,
			Function callback) {
		context.submitRequest(request, wrap(callback));
	}

	public void submitRequest(Function callback, int flag) {
		context.submitRequest(wrap(callback), flag);
	}

	public void submitAlteredRequest(Function callback,
			String value) {
		context.submitAlteredRequest(wrap(callback), value);
	}

	public void submitAlteredRequest(Function callback,
			String value, int flag) {
		context.submitAlteredRequest(wrap(callback), value, flag);
	}

	public void submitAlteredRequest(Function callback,
			String value, boolean append, int flag) {
		context.submitAlteredRequest(wrap(callback), value, append, flag);
	}

	public void submitAlteredParameterNameRequest(
			Function callback, String name, int flag) {
		context.submitAlteredParameterNameRequest(wrap(callback), name, flag);
	}

	public void submitMultipleAlteredRequests(Function callback, String[] injectables) {
		context.submitMultipleAlteredRequests(wrap(callback), injectables);
	}

	public void submitMultipleAlteredRequests(Function callback, String[] injectables, boolean append) {
		context.submitMultipleAlteredRequests(wrap(callback), injectables, append);
	}
	
	public void responseChecks(HttpUriRequest request, IHttpResponse response) {
		context.responseChecks(request, response);
	}
	
	public void contentChecks(HttpUriRequest request, IHttpResponse response) {
		context.contentChecks(request, response);
	}
	
	public void responseChecks(int idx) {
		context.responseChecks(idx);
	}
	
	public void pivotChecks(HttpUriRequest request, IHttpResponse response) {
		context.pivotChecks(request, response);
	}

	private CrawlerCallbackWrapper wrap(Function callback) {
		return new CrawlerCallbackWrapper(callback);
	}
	
	public void publishAlert(String type, String message, HttpUriRequest request, IHttpResponse response) {
		context.publishAlert(type, message, request, response);
	}
	
	public void publishAlert(String type, String key, String message, HttpUriRequest request, IHttpResponse response) {
		context.publishAlert(type, key, message, request, response);
	}
}
