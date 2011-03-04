package com.subgraph.vega.ui.http.interceptviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.commands.InterceptStateSourceProvider;
import com.subgraph.vega.ui.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.httpeditor.RequestRenderer;

public class InterceptView extends ViewPart {
	private final IHttpInterceptProxyEventHandler requestListener;
	private final IHttpInterceptProxyEventHandler responseListener;
//	private Label statusLabel;
	private final RequestRenderer requestRenderer = new RequestRenderer();
	private HttpRequestViewer requestViewer;
	private HttpRequestViewer responseViewer;

	public InterceptView() {
		requestListener = new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				handleTransactionRequest(transaction);
			}
		};
		responseListener = new IHttpInterceptProxyEventHandler() {
			@Override
			public void handleRequest(IProxyTransaction transaction) {
				handleTransactionResponse(transaction);
			}
		};

		IHttpInterceptor interceptor = Activator.getDefault().getProxyService().getInterceptor();
		interceptor.setRequestListener(requestListener);
		interceptor.setResponseListener(responseListener);
}

	@Override
	public void createPartControl(Composite parent) {
		final SashForm form = new SashForm(parent, SWT.VERTICAL);
//		statusLabel = new Label(form, SWT.NONE);
//		statusLabel.setText("");
		createTabFolderRequest(form);
		createTabFolderResponse(form);
//		form.setWeights(new int[] { 2, 49, 49, });
		form.setWeights(new int[] { 50, 50, });
		form.pack();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public void handleTransactionRequest(IProxyTransaction transaction) {
		indicateInterceptPending();
		final String content = requestRenderer.renderRequestText(transaction.getRequest());
		if (requestViewer != null) {
			synchronized(requestViewer) {
				Display display = requestViewer.getControl().getDisplay();
				display.syncExec (new Runnable () {
					public void run () {
						requestViewer.setContent(content);
					}
				});	
			}
		}
	}

	public void handleTransactionResponse(IProxyTransaction transaction) {
		indicateInterceptPending();
		final String content = requestRenderer.renderResponseText(transaction.getResponse().getRawResponse());
		if (responseViewer != null) {
			synchronized(responseViewer) {
				Display display = responseViewer.getControl().getDisplay();
				display.syncExec (new Runnable () {
					public void run () {
						responseViewer.setContent(content);
					}
				});	
			}
		}
	}

	private void indicateInterceptPending() {
		ISourceProviderService sourceProviderService = (ISourceProviderService) PlatformUI.getWorkbench().getService(ISourceProviderService.class);
		InterceptStateSourceProvider interceptState = (InterceptStateSourceProvider) sourceProviderService.getSourceProvider(InterceptStateSourceProvider.INTERCEPT_STATE);
		interceptState.setInterceptPending();
	}

	private Composite createTabFolderRequest(Composite parent) {
		final TabFolder rootControl = new TabFolder(parent, SWT.TOP);

		final TabItem requestItem = new TabItem(rootControl, SWT.NONE);
		requestItem.setText("Request");
		requestViewer = new HttpRequestViewer(rootControl);
		requestItem.setControl(requestViewer.getControl());

		final TabItem optionsItem = new TabItem(rootControl, SWT.NONE);
		optionsItem.setText("Options");
		OptionsViewer requestOptions = new OptionsViewer();
		optionsItem.setControl(requestOptions.createViewer(rootControl));

		return rootControl;
	}

	private Composite createTabFolderResponse(Composite parent) {
		final TabFolder rootControl = new TabFolder(parent, SWT.TOP);

		final TabItem responseItem = new TabItem(rootControl, SWT.NONE);
		responseItem.setText("Response");
		responseViewer = new HttpRequestViewer(rootControl);
		responseItem.setControl(responseViewer.getControl());

//		final TabItem optionsItem = new TabItem(rootControl, SWT.NONE);
//		optionsItem.setText("Options");
//		OptionsViewer requestOptions = new OptionsViewer();
//		optionsItem.setControl(requestOptions.createViewer(rootControl));

		return rootControl;
	}
}
