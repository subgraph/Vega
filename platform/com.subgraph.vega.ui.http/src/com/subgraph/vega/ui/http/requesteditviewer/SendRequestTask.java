package com.subgraph.vega.ui.http.requesteditviewer;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestEngineException;

public class SendRequestTask implements Runnable {

	private final HttpUriRequest request;
	private final RequestEditView view;
	private final IHttpRequestEngine requestEngine;
	private final Thread sendingThread;
	
	SendRequestTask(HttpUriRequest request, RequestEditView view, IHttpRequestEngine requestEngine) {
		this.request = request;
		this.view = view;
		this.requestEngine = requestEngine;
		this.sendingThread = new Thread(this);
	}
	
	void start() {
		sendingThread.start();
	}
	
	public void run() {
		try {
			final IHttpResponse response = requestEngine.sendRequest(request).get(true);
			view.processResponse(response);
		} catch (RequestEngineException e) {
			view.processResponse(null);
			view.displayExceptionError((Exception) e.getCause());
			return;
		}
	}
}
