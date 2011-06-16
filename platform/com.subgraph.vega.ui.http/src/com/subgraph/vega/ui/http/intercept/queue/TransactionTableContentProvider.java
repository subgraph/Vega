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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;

public class TransactionTableContentProvider implements IStructuredContentProvider {
	private final TableViewer viewer;
	private IHttpInterceptor interceptor;
	private IHttpInterceptorEventHandler eventHandler;

	public TransactionTableContentProvider(final TableViewer viewer) {
		this.viewer = viewer;
		eventHandler = new IHttpInterceptorEventHandler() {
			@Override
			public void notifyQueue(IProxyTransaction transaction, int idx) {
				handleUpdate();
			}

			@Override
			public void notifyRemove(int idx) {
				handleUpdate();
			}

			@Override
			public void notifyEmpty() {
				handleUpdate();
			}
		};
	}

	private void handleUpdate() {
		viewer.getTable().getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

	@Override
	public void dispose() {
		if (interceptor != null) {
			interceptor.removeEventHandler(eventHandler);
			interceptor = null;
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (interceptor != null) {
			interceptor.removeEventHandler(eventHandler);
		}

		if (newInput != null) {
			interceptor = (IHttpInterceptor) newInput;
			interceptor.addEventHandler(eventHandler);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return interceptor.getTransactions();
	}

}
