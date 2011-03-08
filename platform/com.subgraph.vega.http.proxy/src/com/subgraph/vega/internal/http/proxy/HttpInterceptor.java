package com.subgraph.vega.internal.http.proxy;

import java.util.ArrayList;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpoint;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.ProxyTransactionDirection;
import com.subgraph.vega.internal.http.proxy.ProxyTransaction;

public class HttpInterceptor implements IHttpInterceptor {
	private IHttpInterceptorEventHandler eventHandler;
	private final ArrayList<IHttpInterceptorBreakpoint> breakpointListRequest = new ArrayList<IHttpInterceptorBreakpoint>();
	private final ArrayList<IHttpInterceptorBreakpoint> breakpointListResponse = new ArrayList<IHttpInterceptorBreakpoint>();
	private final ArrayList<ProxyTransaction> transactionQueue = new ArrayList<ProxyTransaction>(); /**< Queue of intercepted transactions pending processing */

	private ArrayList<IHttpInterceptorBreakpoint> getBreakpointList(ProxyTransactionDirection direction) {
		if (direction == ProxyTransactionDirection.DIRECTION_REQUEST) {
			return breakpointListRequest;
		} else {
			return breakpointListResponse; 
		}
	}

	/**
	 * @return True if the transaction was intercepted and added to the queue for processing, false if it can immediately
	 * be handled.
	 */
	public synchronized boolean handleTransaction(ProxyTransaction transaction) {
		if (eventHandler != null) {
			ArrayList<IHttpInterceptorBreakpoint> breakpointList;
			if (transaction.hasResponse() == false) {
				breakpointList = breakpointListRequest;
			} else {
				breakpointList = breakpointListResponse; 
			}

			for (IHttpInterceptorBreakpoint breakpoint: breakpointList) {
				if (breakpoint.test(transaction) == true) {
					transaction.setPending(this);
					transactionQueue.add(transaction);
					eventHandler.notifyQueue(transaction);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public synchronized void setEventHandler(IHttpInterceptorEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	@Override
	public synchronized IHttpInterceptorBreakpoint createBreakpoint(ProxyTransactionDirection direction, HttpInterceptorBreakpointType breakpointType, HttpInterceptorBreakpointMatchType matchType, String pattern, boolean isEnabled) {
		if ((breakpointType.getMask() & direction.getMask()) == 0) {
			throw new IllegalArgumentException();
		}

		HttpInterceptorBreakpoint breakpoint = new HttpInterceptorBreakpoint(breakpointType, matchType, pattern, isEnabled);
		getBreakpointList(direction).add(breakpoint);
		return breakpoint;
	}

	@Override
	public synchronized void removeBreakpoint(ProxyTransactionDirection direction, IHttpInterceptorBreakpoint breakpoint) {
		getBreakpointList(direction).remove(breakpoint);
	}

	@Override
	public synchronized int getBreakpontIdxOf(ProxyTransactionDirection direction, IHttpInterceptorBreakpoint breakpoint) {
		return getBreakpointList(direction).indexOf(breakpoint);
	}

	@Override
	public synchronized int getBreakpointCnt(ProxyTransactionDirection direction) {
		return getBreakpointList(direction).size();
	}

	@Override
	public synchronized IHttpInterceptorBreakpoint[] getBreakpoints(ProxyTransactionDirection direction) {
		ArrayList<IHttpInterceptorBreakpoint> breakpointList = getBreakpointList(direction);
		return breakpointList.toArray(new HttpInterceptorBreakpoint[breakpointList.size()]);
	}

	@Override
	public synchronized int transactionQueueSize() {
		return transactionQueue.size();
	}

	@Override
	public synchronized IProxyTransaction transactionQueuePop() {
		if (transactionQueue.size() == 0) {
			return null;
		}
		ProxyTransaction transaction = transactionQueue.remove(0);
		transaction.setUnqueued();
		return transaction;
	}

	public synchronized void notifyHandled(ProxyTransaction transaction) {
		transactionQueue.remove(transactionQueue.indexOf(transaction));
	}

}
