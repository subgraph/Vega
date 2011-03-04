package com.subgraph.vega.internal.http.proxy;

import java.util.ArrayList;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpoint;
import com.subgraph.vega.internal.http.proxy.ProxyTransaction;

public class HttpInterceptor implements IHttpInterceptor {
	private IHttpInterceptProxyEventHandler eventHandlerRequest;
	private IHttpInterceptProxyEventHandler eventHandlerResponse;

	private final ArrayList<IHttpInterceptorBreakpoint> breakpointList = new ArrayList<IHttpInterceptorBreakpoint>();
	private final ArrayList<ProxyTransaction> transactionQueue = new ArrayList<ProxyTransaction>();

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
			ProxyTransaction transaction = transactionQueue.get(0);
			for (IHttpInterceptorBreakpoint breakpoint: breakpointList) {
				if (breakpoint.test(transaction) == true) {
					eventHandlerRequest.handleRequest(transactionQueue.get(0));
					return true;
				}
			}
		}
		return false;
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
	public IHttpInterceptorBreakpoint createBreakpoint(HttpInterceptorBreakpointType breakpointType, HttpInterceptorBreakpointMatchType matchType, String pattern, boolean isEnabled) {
		HttpInterceptorBreakpoint breakpoint = new HttpInterceptorBreakpoint(breakpointType, matchType, pattern, isEnabled);
		breakpointList.add(breakpoint);
		return breakpoint;
	}

	@Override
	public void removeBreakpoint(IHttpInterceptorBreakpoint breakpoint) {
		breakpointList.remove(breakpoint);
	}

	@Override
	public int getBreakpontIdxOf(IHttpInterceptorBreakpoint breakpoint) {
		return breakpointList.indexOf(breakpoint);
	}

	@Override
	public int getBreakpointCnt() {
		return breakpointList.size();
	}

	@Override
	public IHttpInterceptorBreakpoint[] getBreakpoints() {
		return breakpointList.toArray(new HttpInterceptorBreakpoint[breakpointList.size()]);
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
