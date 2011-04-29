package com.subgraph.vega.internal.http.proxy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransactionEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class ProxyTransaction implements IProxyTransaction {
	private final IHttpRequestEngine requestEngine;
	private final HttpContext context;
	private IProxyTransactionEventHandler eventHandler;
	private HttpRequest request;
	private HttpUriRequest uriRequest;
	private IHttpResponse response;
    private HttpInterceptor interceptor; 
    private boolean isPending = false;
    private boolean doForward = false;
    private Lock lock = new ReentrantLock();
    private Condition cv = lock.newCondition();

	ProxyTransaction(IHttpRequestEngine requestEngine, HttpContext context) {
		this.requestEngine = requestEngine;
		this.context = context;
	}

	public IHttpRequestEngine getRequestEngine() {
		return requestEngine;
	}

	public HttpContext getContext() {
		return context;
	}

	public synchronized void setRequest(HttpRequest request) {
		this.request = request;
	}

	public synchronized void setResponse(IHttpResponse response) {
		this.response = response;
	}

	public void await() throws InterruptedException {
 		lock.lock();
		try {
			cv.await();
		}
		finally {
			lock.unlock();
		}
	}

	public synchronized void setPending(HttpInterceptor interceptor) {
		this.interceptor = interceptor;
		isPending = true;
		doForward = false;
	}

	public synchronized void setUnqueued() {
		interceptor = null;
	}

	public synchronized boolean getForward() {
		return doForward;
	}

	@Override
	public synchronized void setEventHandler(IProxyTransactionEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	@Override
	public synchronized boolean hasRequest() {
		return (request != null);
	}

	@Override
	public synchronized HttpRequest getRequest() {
		return request;
	}

	@Override
	public void setUriRequest(HttpUriRequest request) {
		this.uriRequest = request;
	}

	@Override
	public synchronized HttpUriRequest getUriRequest() {
		return uriRequest;
	}

	@Override
	public synchronized boolean hasResponse() {
		return (response != null);
	}

	@Override
	public synchronized IHttpResponse getResponse() {
		return response;
	}

	@Override
	public void doForward() {
		lock.lock();
		try {
			if (isPending == true) {
				if (interceptor != null) {
					interceptor.notifyHandled(this);
					interceptor = null;
				}

				isPending = false;
				doForward = true;
				cv.signal();
			}
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public void doDrop() {
		lock.lock();
		try {
			if (isPending == true) {
				if (interceptor != null) {
					interceptor.notifyHandled(this);
					interceptor = null;
				}

				isPending = false;
				doForward = false;
				cv.signal();
			}
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * Signal that the pending transaction is about to be forwarded.
	 */
	public synchronized void signalForward() {
		if (eventHandler != null) {
			eventHandler.notifyForward();
		}
	}

	/**
	 * Signal that the transaction is complete.
	 * 
	 * @param dropped Boolean indicating whether the transaction was dropped.
	 */
	public synchronized void signalComplete(boolean dropped) {
		if (eventHandler != null) {
			eventHandler.notifyComplete(dropped);
		}		
	}
	
}
