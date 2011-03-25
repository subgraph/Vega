package com.subgraph.vega.impl.scanner.state;

import java.util.logging.Logger;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertModel;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class ModuleContext implements IModuleContext {
	private final static Logger logger = Logger.getLogger("scanner");
	private final PathStateManager scanState;
	private final PathStateRequestBuilder requestBuilder;
	private final IPathState pathState;
	private final int currentIndex;
	private final ModuleContextState contextState;

	ModuleContext(PathStateManager scanState,
			PathStateRequestBuilder requestBuilder, IPathState pathState) {
		this.scanState = scanState;
		this.requestBuilder = requestBuilder;
		this.pathState = pathState;
		currentIndex = -1;
		contextState = new ModuleContextState();
	}

	private ModuleContext(ModuleContext ctx, int index) {
		scanState = ctx.scanState;
		requestBuilder = ctx.requestBuilder;
		pathState = ctx.pathState;
		contextState = ctx.contextState;
		currentIndex = index;
	}

	@Override
	public IPathState getPathState() {
		return pathState;
	}

	@Override
	public int incrementResponseCount() {
		return contextState.incrementResponseCount();
	}

	@Override
	public void addRequestResponse(HttpUriRequest request,
			IHttpResponse response) {
		if (currentIndex == -1)
			throw new IllegalStateException(
					"Cannot add request/response because index has not been specified");
		contextState.addRequestResponse(currentIndex, request, response);
	}

	@Override
	public void addRequestResponse(int index, HttpUriRequest request,
			IHttpResponse response) {
		contextState.addRequestResponse(index, request, response);
	}

	@Override
	public HttpUriRequest getSavedRequest(int index) {
		return contextState.getSavedRequest(index);
	}

	@Override
	public IHttpResponse getSavedResponse(int index) {
		return contextState.getSavedResponse(index);
	}

	@Override
	public IPageFingerprint getSavedFingerprint(int index) {
		return contextState.getSavedFingerprint(index);
	}

	@Override
	public boolean isFingerprintMatch(int idx1, int idx2) {
		final IPageFingerprint fp1 = getSavedFingerprint(idx1);
		final IPageFingerprint fp2 = getSavedFingerprint(idx2);
		if (fp1 == null || fp2 == null)
			return false;
		return fp1.isSame(fp2);
	}

	@Override
	public boolean isFingerprintMatch(int idx, IPageFingerprint fp) {
		final IPageFingerprint fp2 = getSavedFingerprint(idx);
		if (fp == null || fp2 == null)
			return false;
		return fp.isSame(fp2);
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void submitRequest(HttpUriRequest request,
			ICrawlerResponseProcessor callback, int index) {
		if(scanState.requestLoggingEnabled())
			submitRequestWithLogging(request, callback, index);
		else
			scanState.getCrawler().submitTask(request, callback, new ModuleContext(this, index));
	}
	
	private void submitRequestWithLogging(HttpUriRequest request, ICrawlerResponseProcessor callback, int index) {
		final RequestLoggingCrawlerCallback wrapper = new RequestLoggingCrawlerCallback(scanState.getRequestLog(), callback);
		scanState.getCrawler().submitTask(request, wrapper, new ModuleContext(this,index));
	}

	@Override
	public void submitRequest(HttpUriRequest request,
			ICrawlerResponseProcessor callback) {
		submitRequest(request, callback, 0);
	}

	@Override
	public void submitRequest(ICrawlerResponseProcessor callback, int flag) {
		final HttpUriRequest req = requestBuilder.createGetRequest();
		if (req != null)
			submitRequest(req, callback, flag);
	}

	@Override
	public void submitAlteredRequest(ICrawlerResponseProcessor callback,
			String value) {
		submitAlteredRequest(callback, value, false, 0);
	}

	@Override
	public void submitAlteredRequest(ICrawlerResponseProcessor callback,
			String value, int flag) {
		submitAlteredRequest(callback, value, false, flag);
	}

	@Override
	public void submitAlteredRequest(ICrawlerResponseProcessor callback,
			String value, boolean append, int flag) {
		final HttpUriRequest req = requestBuilder.createAlteredRequest(value,
				append);
		if (req != null)
			submitRequest(req, callback, flag);
	}

	@Override
	public void submitAlteredParameterNameRequest(
			ICrawlerResponseProcessor callback, String name, int flag) {
		final HttpUriRequest req = requestBuilder
				.createAlteredParameterNameRequest(name);
		if (req != null)
			submitRequest(req, callback, flag);
	}

	@Override
	public void submitMultipleAlteredRequests(
			ICrawlerResponseProcessor callback, String[] injectables) {
		submitMultipleAlteredRequests(callback, injectables, false);
	}

	@Override
	public void submitMultipleAlteredRequests(
			ICrawlerResponseProcessor callback, String[] injectables,
			boolean append) {
		for (int i = 0; i < injectables.length; i++)
			submitAlteredRequest(callback, injectables[i], append, i);
	}

	@Override
	public void setModuleFailed() {
		contextState.setModuleFailed();
	}

	@Override
	public boolean hasModuleFailed() {
		return contextState.hasModuleFailed();
	}

	@Override
	public void error(HttpUriRequest request, IHttpResponse response,
			String message) {
		final long requestId = scanState.getRequestLog().addRequestResponse(request, response.getRawResponse(), response.getHost());
		logger.warning("Error running module: "+ message + " (request logged with id="+ requestId +")");
	}

	@Override
	public void debug(String msg) {
		scanState.debug("[" + pathState.getPath().getUri() + "] " + msg);
	}

	@Override
	public void analyzePage(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzePage(request, response, pathState);
	}

	@Override
	public void responseChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzeContent(request, response, pathState);
		scanState.analyzePage(request, response, pathState);
	}

	@Override
	public void contentChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzeContent(request, response, pathState);
	}

	@Override
	public void responseChecks(int idx) {
		final HttpUriRequest req = getSavedRequest(idx);
		final IHttpResponse res = getSavedResponse(idx);
		if (req != null && res != null)
			responseChecks(req, res);
	}

	@Override
	public void pivotChecks(HttpUriRequest request, IHttpResponse response) {
		scanState.analyzePivot(request, response, pathState);
		scanState.analyzeContent(request, response, pathState);
		scanState.analyzePage(request, response, pathState);
	}
	
	
	public void publishAlert(String type, String message, HttpUriRequest request, IHttpResponse response, Object ...properties) {
		publishAlert(type, null, message, request, response, properties);
	}

	public void publishAlert(String type, String key, String message, HttpUriRequest request, IHttpResponse response, Object ...properties) {
		final IScanAlertModel alertModel = scanState.getScanAlertModel();
		final IRequestLog requestLog = scanState.getRequestLog();
		try {
			alertModel.lock();
			if(key != null && alertModel.hasAlertKey(key))
				return;
			final long requestId = requestLog.addRequestResponse(request, response.getRawResponse(), response.getHost());
			final IScanAlert alert = alertModel.createAlert(type, key, requestId);
			for(int i = 0; (i + 1) < properties.length; i += 2) {
				if(properties[i] instanceof String) {
					alert.setProperty((String) properties[i], properties[i + 1]);
				} else {
					logger.warning("Property key passed to publishAlert() is not a string");
				}
			}
			if(message != null)
				alert.setStringProperty("message", message);
			alertModel.addAlert(alert);
		} finally {
			alertModel.unlock();
		}
	}
}
