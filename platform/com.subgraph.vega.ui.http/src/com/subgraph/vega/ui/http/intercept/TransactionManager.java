package com.subgraph.vega.ui.http.intercept;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransactionEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.ui.http.Activator;

public class TransactionManager {
	private IHttpInterceptor interceptor;
	private IHttpInterceptorEventHandler interceptorEventHandler;
	private IProxyTransactionEventHandler transactionEventHandler;
	private IProxyTransaction currentTransaction;
	private IProxyTransaction currentRequestTransaction;
	private TransactionViewer requestViewer;
	private TransactionViewer responseViewer;
	private IHttpRequestBuilder requestBuilder;
	private IHttpResponseBuilder responseBuilder;
	
	TransactionManager(IHttpInterceptor interceptor) {
		interceptorEventHandler = new IHttpInterceptorEventHandler() {
			@Override
			public void notifyQueue(IProxyTransaction transaction) {
				if (transaction.hasResponse() == false) {
					handleTransactionRequest(transaction);
				} else {
					handleTransactionResponse(transaction);
				}
			}

			@Override
			public void notifyEmpty() {
			}
		};
		transactionEventHandler = new IProxyTransactionEventHandler() {
			@Override
			public void notifyForward() {
				handleTransactionForward();
			}

			@Override
			public void notifyComplete(boolean dropped) {
				handleTransactionComplete();
			}
		};
		
		this.interceptor = interceptor;

		IHttpRequestEngineFactory requestEngineFactory = Activator.getDefault().getHttpRequestEngineFactoryService();
		IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(requestEngineFactory.createConfig());
		requestBuilder = requestEngine.createRequestBuilder();
		responseBuilder = requestEngine.createResponseBuilder();
	}
	
	void setRequestViewer(TransactionViewer viewer) {
		requestViewer = viewer;
	}
	
	void setResponseViewer(TransactionViewer viewer) {
		responseViewer = viewer;
	}

	public void setManagerActive() {
		interceptor.addEventHandler(interceptorEventHandler);
		synchronized(this) {
			getNextTransaction();
		}
	}

	/**
	 * Close this transaction manager prior to the interceptor view being closed. Unregisters the manager as an event
	 * handler in the interceptor and the current transaction, if one exists. 
	 */
	public void close() {
		if (interceptor != null) {
			interceptor.removeEventHandler(interceptorEventHandler);
			if (currentTransaction != null) {
				currentTransaction.setEventHandler(null);
				currentTransaction = null;
				currentRequestTransaction = null;
			}
			interceptor = null;
		}
	}
	
	void setInactive() {
		setRequestInactive();
		setResponseInactive();
	}	
	
	private void handleTransactionRequest(final IProxyTransaction transaction) {
		synchronized(this) {
			if(currentTransaction == null) {
				currentTransaction = transaction;
				currentTransaction.setEventHandler(transactionEventHandler);
				setRequestPending();
			}
		}
	}

	private void handleTransactionResponse(final IProxyTransaction transaction) {
		synchronized(this) {
			if (currentTransaction == null || currentTransaction == transaction) {
				currentTransaction = transaction;
				currentTransaction.setEventHandler(transactionEventHandler);
				setResponsePending();
			}
		}
	}

	private void handleTransactionForward() {
		synchronized(this) {
			if (currentTransaction.hasResponse()) {
				currentTransaction.setEventHandler(null);
				getNextTransaction();
			} else {
				setRequestSent();
			}
		}
	}

	private void handleTransactionComplete() {
		synchronized(this) {
			currentTransaction.setEventHandler(null);
			getNextTransaction();
		}
	}
	
	/**
	 * Must be invoked in a synchronized block.
	 */
	private void getNextTransaction() {
		currentTransaction = interceptor.transactionQueueGet(0);
		if(currentTransaction != null) {
			currentTransaction.setEventHandler(transactionEventHandler);
			if(!currentTransaction.hasResponse()) {
				setRequestPending();
				setResponseInactive();
			} else {
				setResponsePending();
			}
		} else {
			setInactive();
		}
	}
	
	private synchronized void setRequestPending() {
		final String message = "Request pending to "+ getRequestHostPart(currentTransaction.getRequest());
		currentRequestTransaction = currentTransaction;
		try {
			requestBuilder.setFromRequest(currentTransaction.getRequest());
		} catch (URISyntaxException e) {
			// XXX
		}
		requestViewer.notifyUpdate(message, true);
	}
	
	private synchronized void setRequestInactive() {
		currentRequestTransaction = currentTransaction;
		requestBuilder.clear();
		requestViewer.notifyUpdate("No request pending", false);
	}

	private synchronized void setRequestSent() {
		currentRequestTransaction = currentTransaction;
		requestViewer.notifyUpdate("Request sent, awaiting response", true);
	}
	
	private String getRequestHostPart(HttpRequest request) {
		URI uri;
		try {
			uri = new URI(request.getRequestLine().getUri());
		} catch (URISyntaxException e) {
			return new String("unknown host - error parsing URI");
		}
		String httpHost = uri.getScheme() + "://" + uri.getHost();
		if (uri.getPort() != -1) {
			httpHost += ":" + uri.getPort();
		}
		return httpHost;
	}

	synchronized void forwardRequest() throws URISyntaxException, UnsupportedEncodingException {
		HttpUriRequest request = requestBuilder.buildRequest();
		if (request != null) {
			currentTransaction.setRequest(request);
			currentTransaction.doForward();
		}
	}

	synchronized void forwardResponse() throws UnsupportedEncodingException {
		HttpResponse response = responseBuilder.buildResponse();
		if (response != null) {
			currentTransaction.getResponse().setRawResponse(response);
			currentTransaction.doForward();
		}
	}
	
	synchronized void dropRequest() {
		currentTransaction.doDrop();
	}

	synchronized void dropResponse() {
		currentTransaction.doDrop();
	}

	private synchronized void setResponseInactive() {
		currentRequestTransaction = null;
		responseBuilder.clear();
		responseViewer.notifyUpdate("No response pending", false);
	}

	private synchronized void setResponsePending() {
		responseBuilder.setFromResponse(currentTransaction.getResponse().getRawResponse());
		if(currentRequestTransaction != currentTransaction) {
			setRequestSent();
		}
		responseViewer.notifyUpdate("Reponse pending", true);
	}

	IHttpRequestBuilder getRequestBuilder() {
		return requestBuilder;
	}
	
	IHttpResponseBuilder getResponseBuilder() {
		return responseBuilder;
	}
	
}
