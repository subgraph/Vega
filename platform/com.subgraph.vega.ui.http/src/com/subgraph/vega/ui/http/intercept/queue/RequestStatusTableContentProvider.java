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
package com.subgraph.vega.ui.http.intercept.queue;

import java.util.ArrayList;
import java.util.List;

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
	private final IHttpProxyServiceEventHandler proxyServiceEventHandler;
	private Viewer viewer;
	private IHttpProxyService proxyService;
	private List<IHttpProxyListener> listenerList;
	private List<IHttpRequestTask> requestList;

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
				unregisterProxyListener(listener);
			}

			@Override
			public void notifyConfigChange(int numListeners) {
			}
		};
		listenerList = new ArrayList<IHttpProxyListener>();
		requestList = new ArrayList<IHttpRequestTask>();
	}
	
	@Override
	public void dispose() {
		for (IHttpProxyListener listener: listenerList) {
			unregisterProxyListener(listener);
		}
		listenerList.clear();
		if (proxyService != null) {
			proxyService.unregisterEventHandler(proxyServiceEventHandler);
			proxyService = null;
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (proxyService != null) {
			proxyService.unregisterEventHandler(proxyServiceEventHandler);
			for (IHttpProxyListener listener: listenerList) {
				unregisterProxyListener(listener);
			}
			listenerList.clear();
		}

		proxyService = (IHttpProxyService) newInput;
		if (proxyService != null) {
			proxyService.registerEventHandler(proxyServiceEventHandler);
			for (IHttpProxyListener listener: proxyService.getListeners()) {
				registerProxyListener(listener);
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return requestList.toArray(new Object[0]);
	}

	@Override
	public void handleEvent(IEvent event) {
		if (event instanceof RequestTaskStartEvent) {
			requestList.add(((RequestTaskStartEvent) event).getRequestTask());
		} else if (event instanceof RequestTaskStopEvent) {
			requestList.remove(((RequestTaskStopEvent) event).getRequestTask());
		}
		if (viewer != null) {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					viewer.refresh();
				}
			});
		}
	}

	private void registerProxyListener(IHttpProxyListener listener) {
		listenerList.add(listener);
		listener.getRequestEngine().addRequestListener(this);
	}

	private void unregisterProxyListener(IHttpProxyListener listener) {
		listener.getRequestEngine().removeRequestListener(this);
	}
	
}
