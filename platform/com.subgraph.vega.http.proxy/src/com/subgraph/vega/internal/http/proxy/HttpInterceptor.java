package com.subgraph.vega.internal.http.proxy;

import java.util.ArrayList;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointType;
import com.subgraph.vega.api.http.proxy.HttpInterceptorLevel;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpoint;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.ProxyTransactionDirection;
import com.subgraph.vega.internal.http.proxy.ProxyTransaction;

public class HttpInterceptor implements IHttpInterceptor {
	private IHttpInterceptorEventHandler eventHandler;
	private HttpInterceptorLevel interceptorLevelRequest = HttpInterceptorLevel.DISABLED;
	private HttpInterceptorLevel interceptorLevelResponse = HttpInterceptorLevel.DISABLED;
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

	private boolean interceptBreakpoint(ArrayList<IHttpInterceptorBreakpoint> breakpointList, ProxyTransaction transaction) {
		for (IHttpInterceptorBreakpoint breakpoint: breakpointList) {
			if (breakpoint.test(transaction) == true) {
				return true;
			}
		}
		return false;
	}

	private boolean intercept(ProxyTransaction transaction) {
		if (transaction.hasResponse() == false) {
			if (interceptorLevelRequest == HttpInterceptorLevel.ENABLED_ALL) {
				return true;
			} else if (interceptorLevelRequest == HttpInterceptorLevel.ENABLED_BREAKPOINTS) {
				return interceptBreakpoint(breakpointListRequest, transaction);
			}
		} else {
			if (interceptorLevelResponse == HttpInterceptorLevel.ENABLED_ALL) {
				return true;
			} else if (interceptorLevelResponse == HttpInterceptorLevel.ENABLED_BREAKPOINTS) {
				return interceptBreakpoint(breakpointListResponse, transaction);
			}
		}
		return false;
	}

	/**
	 * @return True if the transaction was intercepted and added to the queue for processing, false if it can immediately
	 * be handled.
	 */
	public synchronized boolean handleTransaction(ProxyTransaction transaction) {
		if (eventHandler != null && intercept(transaction) != false) {
			transaction.setPending(this);
			transactionQueue.add(transaction);
			eventHandler.notifyQueue(transaction);
			return true;
		}
		return false;
	}

	@Override
	public synchronized void setEventHandler(IHttpInterceptorEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	@Override
	public synchronized void setInterceptLevel(ProxyTransactionDirection direction, HttpInterceptorLevel level) {
		if (direction == ProxyTransactionDirection.DIRECTION_REQUEST) {
			interceptorLevelRequest = level;
		} else {
			interceptorLevelResponse = level;
		}
	}

	@Override
	public synchronized HttpInterceptorLevel getInterceptLevel( ProxyTransactionDirection direction) {
		if (direction == ProxyTransactionDirection.DIRECTION_REQUEST) {
			return interceptorLevelRequest;
		} else {
			return interceptorLevelResponse;
		}
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
