package com.subgraph.vega.internal.http.proxy;

import java.net.URI;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class ProxyTransaction implements IProxyTransaction {
	private final HttpContext context;
	private HttpRequest request;
	private URI uri;
	private IHttpResponse response;
	private HttpHost httpHost;
    private Lock lock = new ReentrantLock();
    private Condition cv = lock.newCondition();
    private boolean doForward = false;

	ProxyTransaction(HttpContext context) {
		this.context = context;
	}

	public HttpContext getContext() {
		return context;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public void setResponse(IHttpResponse response) {
		this.response = response;
	}

	public void setHttpHost(HttpHost httpHost) {
		this.httpHost = httpHost;
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
	
	public void signalForward() {
		lock.lock();
		try {
			doForward = true;
			cv.signal();
		}
		finally {
			lock.unlock();
		}
	}

	public void signalDrop() {
		lock.lock();
		try {
			doForward = false;
			cv.signal();
		}
		finally {
			lock.unlock();
		}
	}

	public boolean getForward() {
		return doForward;
	}
	
	@Override
	public HttpHost getHttpHost() {
		return httpHost;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public boolean hasRequest() {
		return (request != null);
	}

	@Override
	public HttpRequest getRequest() {
		return request;
	}

	@Override
	public boolean hasResponse() {
		return (response != null);
	}

	@Override
	public IHttpResponse getResponse() {
		return response;
	}
}
