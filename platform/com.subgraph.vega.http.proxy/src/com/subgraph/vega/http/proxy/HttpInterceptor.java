package com.subgraph.vega.http.proxy;

import java.util.List;
import java.util.ArrayList;

import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.internal.http.proxy.ProxyTransaction;

public class HttpInterceptor implements IHttpInterceptor {
	private IHttpInterceptProxyEventHandler eventHandlerRequest;
	private IHttpInterceptProxyEventHandler eventHandlerResponse;
	private final List<ProxyTransaction> transactionQueue = new ArrayList<ProxyTransaction>();

	public boolean queueTransaction(ProxyTransaction transaction) throws InterruptedException {
		synchronized(transactionQueue) {
			transactionQueue.add(transaction);

			boolean rv;
			if (transaction.hasResponse() == false) {
				rv = handleRequest();
			} else {
				rv = handleResponse();
			}

			if (rv == false) {
				return false;
			}
		}

		transaction.await();
		return true;
	}

	private boolean handleRequest() {
		if (eventHandlerRequest != null) {
			eventHandlerRequest.handleRequest(transactionQueue.get(0));
			return true;
		} else {
			return false;
		}
	}
	
	private boolean handleResponse() {
		if (eventHandlerResponse != null) {
			eventHandlerResponse.handleRequest(transactionQueue.get(0));
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void setRequestListener(IHttpInterceptProxyEventHandler handler) {
		eventHandlerRequest = handler;
	}

	@Override
	public void unsetRequestListener(IHttpInterceptProxyEventHandler handler) {
		eventHandlerRequest = null;
	}

	@Override
	public void setResponseListener(IHttpInterceptProxyEventHandler handler) {
		eventHandlerResponse = handler;		
	}

	@Override
	public void unsetResponseListener(IHttpInterceptProxyEventHandler handler) {
		eventHandlerResponse = null;
	}

	@Override
	public void forwardPending() {
		synchronized(transactionQueue) {
			if (!transactionQueue.isEmpty()) {
				ProxyTransaction transaction = transactionQueue.remove(0);
				transaction.signalForward();
			}
		}
	}

	@Override
	public void dropPending() {
		synchronized(transactionQueue) {
			if (!transactionQueue.isEmpty()) {
				ProxyTransaction transaction = transactionQueue.remove(0);
				transaction.signalDrop();
			}
		}

	}
}
