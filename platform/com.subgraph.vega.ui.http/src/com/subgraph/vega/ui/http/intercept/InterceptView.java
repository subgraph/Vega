package com.subgraph.vega.ui.http.intercept;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IProxyTransaction.TransactionDirection;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.ui.http.Activator;

public class InterceptView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		final IHttpInterceptor interceptor = Activator.getDefault().getProxyService().getInterceptor();
		final IModel model = Activator.getDefault().getModel();
		final SashForm form = new SashForm(parent, SWT.VERTICAL);
		final TransactionManager transactionManager = new TransactionManager(interceptor);
		new TransactionViewer(form, model, transactionManager, TransactionDirection.DIRECTION_REQUEST);
		new TransactionViewer(form, model, transactionManager, TransactionDirection.DIRECTION_RESPONSE);
		transactionManager.setInactive();
		form.setWeights(new int[] { 50, 50, });
		form.pack();
	}

	@Override
	public void setFocus() {
	}
}
