/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.http.statusview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpProxyListener;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IHttpProxyServiceEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestTask;
import com.subgraph.vega.api.http.requests.RequestTaskStartEvent;
import com.subgraph.vega.api.http.requests.RequestTaskStopEvent;

public class RequestStatusTableContentProvider implements IStructuredContentProvider, IEventHandler {
	private static final long REQUEST_EXPIRY = 15000; // time in milliseconds before a finished request is removed from requestList
	private final IHttpProxyServiceEventHandler proxyServiceEventHandler;
	private final Timer refreshTimer;
	private TimerTask refreshTimerTask;
	private Viewer viewer;
	private IHttpProxyService proxyService;
	private final List<IHttpProxyListener> listenerList;
	private final List<IHttpRequestTask> requestList;
	private int requestListExpiryIdx; // index of first completed request task in requestList 

	public RequestStatusTableContentProvider() {
		proxyServiceEventHandler = new IHttpProxyServiceEventHandler() {
			@Override
			public void notifyStart(int numListeners) {
			}

			@Override
			public void notifyStartListener(IHttpProxyListener listener) {
				registerProxyListener(listener);
			}

			@Override
			public void notifyStop() {
			}

			@Override
			public void notifyStopListener(IHttpProxyListener listener) {
				unregisterProxyListener(listener, true);
			}

			@Override
			public void notifyConfigChange(int numListeners) {
			}
		};
		refreshTimer = new Timer();
		listenerList = new ArrayList<IHttpProxyListener>();
		requestList = new ArrayList<IHttpRequestTask>();
		requestListExpiryIdx = 0;
	}
	
	@Override
	public void dispose() {
		for (IHttpProxyListener listener: listenerList) {
			unregisterProxyListener(listener, false);
		}
		synchronized(this) {
			listenerList.clear();
		}
		if (proxyService != null) {
			proxyService.unregisterEventHandler(proxyServiceEventHandler);
			proxyService = null;
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		synchronized(this) {
			this.viewer = viewer;
			if (proxyService != null) {
				proxyService.unregisterEventHandler(proxyServiceEventHandler);
				for (IHttpProxyListener listener: listenerList) {
					unregisterProxyListener(listener, false);
				}
				listenerList.clear();
				requestList.clear();
				requestListExpiryIdx = 0;
			}

			proxyService = (IHttpProxyService) newInput;
			if (proxyService != null) {
				proxyService.registerEventHandler(proxyServiceEventHandler);
				for (IHttpProxyListener listener: proxyService.getListeners()) {
					registerProxyListener(listener);
				}
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		synchronized(this) {
			return requestList.toArray(new Object[0]);
		}
	}

	@Override
	public void handleEvent(IEvent event) {
		synchronized(this) {
			if (event instanceof RequestTaskStartEvent) {
				requestList.add(0, ((RequestTaskStartEvent) event).getRequestTask());
				requestListExpiryIdx++;
			} else if (event instanceof RequestTaskStopEvent) {
				final IHttpRequestTask requestTask = (((RequestTaskStopEvent) event).getRequestTask());
				final int idx = requestList.indexOf(requestTask);
				if (idx != -1) {
					requestListExpiryIdx--;
					requestList.remove(idx);
					requestList.add(requestListExpiryIdx, requestTask);
					if (refreshTimerTask == null) {
						refreshTimerTask = createRefreshTask();
						refreshTimer.schedule(refreshTimerTask, REQUEST_EXPIRY);
					}
				}
			}
			if (viewer != null) {
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					public void run() {
						viewer.refresh();
					}
				});
			}
		}
	}

	private void registerProxyListener(IHttpProxyListener listener) {
		synchronized(this) {
			listenerList.add(listener);
		}
		listener.getRequestEngine().addRequestListener(this);
	}

	private void unregisterProxyListener(IHttpProxyListener listener, boolean remove) {
		listener.getRequestEngine().removeRequestListener(this);
		if (remove) {
			synchronized(this) {
				listenerList.remove(listener);
			}
		}
	}

	private TimerTask createRefreshTask() {
		return new TimerTask() {
			@Override
			public void run() {
				synchronized(RequestStatusTableContentProvider.this) {
					final Date now = new Date();
					int idx;
					for (idx = requestList.size() - 1; idx >= requestListExpiryIdx; idx--) {
						final IHttpRequestTask requestTask = requestList.get(idx);
						final long diff = now.getTime() - requestTask.getTimeCompleted().getTime();
						if (diff >= REQUEST_EXPIRY) {
							requestList.remove(idx);
						} else {
							break;
						}
					}
					if (idx != requestListExpiryIdx - 1) {
						final IHttpRequestTask requestTask = requestList.get(idx);
						refreshTimerTask = createRefreshTask();
						refreshTimer.schedule(refreshTimerTask, requestTask.getTimeCompleted().getTime() + REQUEST_EXPIRY - now.getTime());
					} else {
						refreshTimerTask = null;
					}
					if (viewer != null) {
						viewer.getControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								viewer.refresh();
							}
						});
					}
				}
			}
		};
	}
	
}
