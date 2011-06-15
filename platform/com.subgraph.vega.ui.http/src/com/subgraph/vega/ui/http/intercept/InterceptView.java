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
package com.subgraph.vega.ui.http.intercept;

import org.apache.http.client.HttpClient;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.ui.http.Activator;

public class InterceptView extends ViewPart {
	public final static String VIEW_ID = "com.subgraph.vega.views.intercept";
	private SashForm parentComposite;
	private TransactionManager transactionManager;
	private TransactionInfo transactionInfo;
	private TransactionViewer transactionViewerRequest;
	private TransactionViewer transactionViewerResponse;

	@Override
	public void createPartControl(Composite parent) {
		final IHttpInterceptor interceptor = Activator.getDefault().getProxyService().getInterceptor();
		final IModel model = Activator.getDefault().getModel();
		parentComposite = new SashForm(parent, SWT.VERTICAL);
		transactionManager = new TransactionManager(this, interceptor);

		// REVISIT: shouldn't need to instantiate a request engine to get builders
		IHttpRequestEngineFactory requestEngineFactory = Activator.getDefault().getHttpRequestEngineFactoryService();
		HttpClient client = requestEngineFactory.createBasicClient();
		IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(client, requestEngineFactory.createConfig());
		transactionInfo = new TransactionInfo(requestEngine);
		
		transactionViewerRequest = new TransactionViewer(parentComposite, model, interceptor, transactionManager, transactionInfo, TransactionDirection.DIRECTION_REQUEST);
		transactionViewerResponse = new TransactionViewer(parentComposite, model, interceptor, transactionManager, transactionInfo, TransactionDirection.DIRECTION_RESPONSE);
		transactionManager.setManagerActive();
		parentComposite.setWeights(new int[] { 50, 50, });
		parentComposite.pack();
	}

	@Override
	public void dispose() {
		if (transactionManager != null) {
			transactionManager.close();
		}
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	public void openTransaction(IProxyTransaction transaction) {
		transactionManager.openTransaction(transaction);
	}

	public void notifyUpdate() {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				doUpdate();
			}
		});
	}
	
	private void doUpdate() {
		transactionManager.updateTransactionInfo(transactionInfo);
		transactionViewerRequest.notifyUpdate();
		transactionViewerResponse.notifyUpdate();
	}
	
}
