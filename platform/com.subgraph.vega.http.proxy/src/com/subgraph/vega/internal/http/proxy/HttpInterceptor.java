package com.subgraph.vega.internal.http.proxy;

import java.util.ArrayList;

import org.apache.http.HttpResponse;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.proxy.HttpInterceptorLevel;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.conditions.ConditionSetChanged;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

import com.subgraph.vega.internal.http.proxy.ProxyTransaction;

public class HttpInterceptor implements IHttpInterceptor {
	private IHttpInterceptorEventHandler eventHandler;
	private HttpInterceptorLevel interceptorLevelRequest = HttpInterceptorLevel.DISABLED;
	private HttpInterceptorLevel interceptorLevelResponse = HttpInterceptorLevel.DISABLED;
	private final Object breakpointSetLock = new Object();
	private IHttpConditionSet breakpointSetRequest; 
	private IHttpConditionSet breakpointSetResponse; 
	private final ArrayList<ProxyTransaction> transactionQueue = new ArrayList<ProxyTransaction>(); /**< Queue of intercepted transactions pending processing */

	
	HttpInterceptor(IModel model) {
		breakpointSetRequest = createConditionSet(model, true);
		breakpointSetResponse = createConditionSet(model, false);
	}

	private IHttpConditionSet createConditionSet(IModel model, boolean isRequestSet) {
		final String name = (isRequestSet) ? (IHttpConditionManager.CONDITION_SET_BREAKPOINTS_REQUEST) : (IHttpConditionManager.CONDITION_SET_BREAKPOINTS_RESPONSE);
		return model.addConditionSetTracker(name, createConditionSetChangedHandler(isRequestSet));
	}

	private IEventHandler createConditionSetChangedHandler(final boolean isRequestSet) {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof ConditionSetChanged) 
					onConditionSetChanged((ConditionSetChanged) event, isRequestSet);
			}
		};
	}

	private void onConditionSetChanged(ConditionSetChanged event, boolean isRequestSet) {
		synchronized(breakpointSetLock) {
			if(isRequestSet)
				breakpointSetRequest = event.getConditionSet();
			else
				breakpointSetResponse = event.getConditionSet();
		}
	}

	private boolean intercept(ProxyTransaction transaction) {
		if (transaction.hasResponse() == false) 
			return interceptByLevelAndBreakpointSet(transaction, interceptorLevelRequest, breakpointSetRequest);
		else
			return interceptByLevelAndBreakpointSet(transaction, interceptorLevelResponse, breakpointSetResponse);				
	}
	
	private boolean interceptByLevelAndBreakpointSet(ProxyTransaction transaction, HttpInterceptorLevel level, IHttpConditionSet breakpointSet) {
		switch(level) {
		case ENABLED_ALL:
			return true;
		case DISABLED:
			return false;
		case ENABLED_BREAKPOINTS:
			return interceptOnBreakpointSet(breakpointSet, transaction);
		}
		return false;
	}

	private boolean interceptOnBreakpointSet(IHttpConditionSet breakpointSet, ProxyTransaction transaction) {
		final HttpResponse response = (transaction.hasResponse()) ? (transaction.getResponse().getRawResponse()) : (null);
		synchronized(breakpointSetLock) {
			return (breakpointSet == null) ? (false) : (breakpointSet.matches(transaction.getRequest(), response));
		}
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
