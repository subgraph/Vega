package com.subgraph.vega.internal.http.proxy;

import java.util.ArrayList;

import com.subgraph.vega.api.http.proxy.HttpInterceptorLevel;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

import com.subgraph.vega.internal.http.proxy.ProxyTransaction;

public class HttpInterceptor implements IHttpInterceptor {
	private IHttpInterceptorEventHandler eventHandler;
	private HttpInterceptorLevel interceptorLevelRequest = HttpInterceptorLevel.DISABLED;
	private HttpInterceptorLevel interceptorLevelResponse = HttpInterceptorLevel.DISABLED;
	private final IHttpConditionSet breakpointSetRequest; 
	private final IHttpConditionSet breakpointSetResponse; 
	private final ArrayList<ProxyTransaction> transactionQueue = new ArrayList<ProxyTransaction>(); /**< Queue of intercepted transactions pending processing */

	
	HttpInterceptor(IHttpConditionManager conditionManager) {
		breakpointSetRequest = conditionManager.createConditionSet();
		breakpointSetResponse = conditionManager.createConditionSet();
	}

	private boolean intercept(ProxyTransaction transaction) {
		if (transaction.hasResponse() == false) {
			if (interceptorLevelRequest == HttpInterceptorLevel.ENABLED_ALL) {
				return true;
			} else {
				if (interceptorLevelRequest != HttpInterceptorLevel.ENABLED_BREAKPOINTS) {
					return false;
				}
			}
		} else {
			if (interceptorLevelResponse == HttpInterceptorLevel.ENABLED_ALL) {
				return true;
			} else {
				if (interceptorLevelResponse != HttpInterceptorLevel.ENABLED_BREAKPOINTS) {
					return false;
				}
			}
		}
		return breakpointSetRequest.matches(transaction.getRequest(), transaction.getResponse().getRawResponse());
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
	public synchronized void setInterceptLevel(TransactionDirection direction, HttpInterceptorLevel level) {
		if (direction == TransactionDirection.DIRECTION_REQUEST) {
			interceptorLevelRequest = level;
		} else {
			interceptorLevelResponse = level;
		}
	}

	@Override
	public synchronized HttpInterceptorLevel getInterceptLevel( TransactionDirection direction) {
		if (direction == TransactionDirection.DIRECTION_REQUEST) {
			return interceptorLevelRequest;
		} else {
			return interceptorLevelResponse;
		}
	}

	@Override
	public IHttpConditionSet getBreakpointSet(TransactionDirection direction) {
		if (direction == TransactionDirection.DIRECTION_REQUEST) {
			return breakpointSetRequest;
		} else {
			return breakpointSetResponse; 
		}
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
