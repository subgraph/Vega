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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.commands.InterceptQueueStateSourceProvider;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class InterceptView extends ViewPart {
	public final static String ID = "com.subgraph.vega.views.intercept";
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
		IHttpRequestEngineFactory requestEngineFactory = Activator.getDefault().getHttpRequestEngineFactoryService();
		transactionInfo = new TransactionInfo(requestEngineFactory.createRequestBuilder(), requestEngineFactory.createResponseBuilder());
		transactionManager.updateTransactionInfo(transactionInfo);

		transactionViewerRequest = new TransactionViewer(parentComposite, model, transactionInfo, TransactionDirection.DIRECTION_REQUEST);
		transactionViewerResponse = new TransactionViewer(parentComposite, model, transactionInfo, TransactionDirection.DIRECTION_RESPONSE);
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
		ISourceProviderService sourceProviderService = (ISourceProviderService) getViewSite().getWorkbenchWindow().getService(ISourceProviderService.class);
		InterceptQueueStateSourceProvider provider = (InterceptQueueStateSourceProvider) sourceProviderService.getSourceProvider(InterceptQueueStateSourceProvider.INTERCEPT_QUEUE_STATE);
		if (transactionInfo.isPending() == true) {
			provider.setPending(true);
		} else {
			provider.setSent(transactionInfo.getRequestStatus() == TransactionManager.TransactionStatus.STATUS_SENT);
		}
		transactionViewerRequest.notifyUpdate();
		transactionViewerResponse.notifyUpdate();
	}

	public void forwardTransaction() {
		try {
			transactionViewerRequest.processChanges();
			transactionViewerResponse.processChanges();
			transactionManager.forwardTransaction(transactionInfo);
		} catch (Exception ex) {
			ErrorDialog.displayExceptionError(parentComposite.getShell(), ex);
			return;
		}
	}

	public void dropTransaction() {
		try {
			transactionManager.dropTransaction(transactionInfo);
		} catch (Exception ex) {
			ErrorDialog.displayExceptionError(parentComposite.getShell(), ex);
			return;
		}
	}

}
